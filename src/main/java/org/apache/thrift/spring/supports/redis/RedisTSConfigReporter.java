package org.apache.thrift.spring.supports.redis;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.TSConfigReporter;
import org.apache.thrift.spring.supports.redis.RedisExecutor.Invoker;
import redis.clients.jedis.Jedis;

/**
 * 基于Redis实现的配置报告
 *
 * @author Arvin
 * @time 2017/3/2 15:47
 */
public class RedisTSConfigReporter implements TSConfigReporter {

    /** 存储信息的前缀 */
    private final String prefix;

    /** 执行器 */
    private final RedisExecutor redisExecutor;

    public RedisTSConfigReporter(String prefix, RedisExecutor redisExecutor) {
        this.prefix = prefix;
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
                return jedis.sadd(prefix, TSConfig.serialize(config));
            }
        });
    }

    @Override
    public void remove(final TSConfig config) {
        this.redisExecutor.execute(new Invoker<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                return jedis.srem(prefix, TSConfig.serialize(config));
            }
        });
    }

    @Override
    public void clear() {
        this.redisExecutor.execute(new Invoker<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                return jedis.del(prefix);
            }
        });
    }
}
