package org.apache.thrift.spring.server.redis;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.server.TSConfigReporter;
import org.apache.thrift.spring.supports.redis.RedisExecutor;
import org.apache.thrift.spring.supports.redis.RedisExecutor.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 基于Redis实现的配置报告, 使用发布订阅模式，为发布端
 *
 * @author Arvin
 * @time 2017/3/2 15:47
 */
public class RedisPubTSConfigReporter implements TSConfigReporter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 存储配置集合的前缀 */
    private final String prefix;

    /** 发布订阅中，发布消息的渠道 */
    private final String channel;

    /** 执行器 */
    private final RedisExecutor redisExecutor;

    public RedisPubTSConfigReporter(String prefix, String channel, RedisExecutor redisExecutor) {
        this.prefix = prefix;
        this.channel = channel;
        this.redisExecutor = redisExecutor;
    }

    public String getPrefix() {
        return prefix;
    }

    public RedisExecutor getRedisExecutor() {
        return redisExecutor;
    }

    @Override
    public void report(final TSConfig config) {
        this.redisExecutor.execute(new Invoker<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                String serialize = TSConfig.serialize(config);
                Long ret = jedis.sadd(prefix, serialize);

                // 发布消息, A表示添加
                String message = "A:" + serialize;
                logger.debug("Redis Publish Message, channel=" + channel + ", message=" + message);
                jedis.publish(channel, message);
                return ret;
            }
        });
    }

    @Override
    public void remove(final TSConfig config) {
        this.redisExecutor.execute(new Invoker<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                String serialize = TSConfig.serialize(config);
                Long ret = jedis.srem(prefix, serialize);
                // 发布消息, A表示添加, D 表示删除
                String message = "D:" + serialize;
                logger.debug("Redis Publish Message, channel=" + channel + ", message=" + message);
                jedis.publish(channel, message);
                return ret;
            }
        });
    }

    @Override
    public void clear() {
        this.redisExecutor.execute(new Invoker<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                Long ret = jedis.del(prefix);
                // 发布消息，清空列表了，C表示清空
                jedis.publish(channel, "C");
                logger.debug("Redis Publish Message, channel=" + channel + ", message=C");
                return ret;
            }
        });
    }
}
