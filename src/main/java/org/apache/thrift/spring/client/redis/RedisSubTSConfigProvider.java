package org.apache.thrift.spring.client.redis;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.client.impl.AbstractTSConfigProvider;
import org.apache.thrift.spring.supports.redis.RedisExecutor;
import org.apache.thrift.spring.utils.Utils;
import org.apache.thrift.spring.supports.redis.RedisExecutor.Invoker;
import org.springframework.beans.factory.DisposableBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订阅者模式实现服务器列表的刷新
 *
 * @author Arvin
 * @time 2017/3/2 16:42
 */
public class RedisSubTSConfigProvider extends AbstractTSConfigProvider implements DisposableBean {

    /** 存储信息的前缀 */
    private final String prefix;

    /** 订阅的频道 */
    private final String channel;

    /** 执行器 */
    private final RedisExecutor redisExecutor;

    /** 线程池 */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RedisSubTSConfigProvider(String prefix, String channel, RedisExecutor redisExecutor) {
        this.prefix = prefix;
        this.channel = channel;
        this.redisExecutor = redisExecutor;
        // 先进行同步
        reloadConfig();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // 初始化订阅者
                initSubscriber();
            }
        });
    }

    private void reloadConfig() {
        logger.debug("---------------------------------------------------------------------------------------------------------");
        logger.debug("重新同步加载Thrift服务器列表.....");
        List<TSConfig> configList = syncLoadTSConfig();
        if (!configList.isEmpty()) {
            setConfigList(configList);
        }
        logger.debug("重新同步加载Thrift服务器列表成功.....");
        logger.debug("新的服务器列表：");
        for (TSConfig config : configList) {
            logger.debug(TSConfig.serialize(config));
        }
        logger.debug("---------------------------------------------------------------------------------------------------------");
    }

    /**
     * 初始化订阅者
     */
    private void initSubscriber() {
        this.redisExecutor.execute(new Invoker<Void>() {
            @Override
            public Void invoke(Jedis jedis) {
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        // 处理消息，不管添加还是删除，都重新加载
                        logger.info("收到[" + channel + "] 的订阅消息 message: " + message);
                        reloadConfig();
                    }

                    @Override
                    public void onPMessage(String pattern, String channel, String message) {

                    }

                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        logger.info("已经订阅了渠道： " + channel);
                    }

                    @Override
                    public void onUnsubscribe(String channel, int subscribedChannels) {

                    }

                    @Override
                    public void onPUnsubscribe(String pattern, int subscribedChannels) {

                    }

                    @Override
                    public void onPSubscribe(String pattern, int subscribedChannels) {

                    }
                };
                jedis.subscribe(jedisPubSub, channel);
                logger.info("释放渠道的订阅： " + channel);
                return null;
            }
        });
    }

    /**
     * 同步加载配置列表
     *
     * @return
     */
    protected List<TSConfig> syncLoadTSConfig() {
        return redisExecutor.execute(new RedisExecutor.Invoker<List<TSConfig>>() {
            @Override
            public List<TSConfig> invoke(Jedis jedis) {
                List<TSConfig> configList = new ArrayList<>();
                Set<String> members = jedis.smembers(prefix);
                if (null != members && !members.isEmpty()) {
                    for (String member : members) {
                        if (Utils.isNotBlank(member)) {
                            TSConfig config = TSConfig.deserialize(member);
                            if (null != config) {
                                // 根据权重设置到列表
                                for (int i = 0; i < config.getWeight(); ++i) {
                                    configList.add(config);
                                }
                            }

                        }
                    }
                }
                return configList;
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }
}
