package org.apache.thrift.spring.supports.redis;

import redis.clients.jedis.Jedis;

/**
 * Redis执行器
 *
 * @author Arvin
 * @time 2017/3/2 16:14
 */
public class RedisExecutor {

    /**
     * jedis命令执行器
     */
    public interface Invoker<T> {
        /**
         * 执行命令
         *
         * @param jedis jedis对象
         * @return
         */
        T invoke(Jedis jedis);
    }

    private JedisManager jedisManager;

    public RedisExecutor(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    /**
     * 执行Redis命令
     *
     * @param invoker 执行器
     * @param <T>     返回类型
     * @return
     */
    public <T> T execute(Invoker<T> invoker) {
        Jedis jedis = null;
        try {
            jedis = jedisManager.get();
            return invoker.invoke(jedis);
        } finally {
            jedisManager.close(jedis);
        }
    }
}
