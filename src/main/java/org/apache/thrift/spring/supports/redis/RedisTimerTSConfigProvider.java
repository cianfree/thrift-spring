package org.apache.thrift.spring.supports.redis;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.impl.AbstractTimerTSConfigProvider;
import org.apache.thrift.spring.utils.Utils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时刷新配置
 *
 * @author Arvin
 * @time 2017/3/2 16:42
 */
public class RedisTimerTSConfigProvider extends AbstractTimerTSConfigProvider {

    /** 存储信息的前缀 */
    private final String prefix;

    /** 执行器 */
    private final RedisExecutor redisExecutor;

    public RedisTimerTSConfigProvider(int interval, String prefix, RedisExecutor redisExecutor) {
        super(interval);
        this.prefix = prefix;
        this.redisExecutor = redisExecutor;
    }

    @Override
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
}
