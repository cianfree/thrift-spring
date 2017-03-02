package org.apache.thrift.spring.supports.redis;

import redis.clients.jedis.Jedis;

/**
 * Jedis 管理
 *
 * @author Arvin
 * @time 2017/3/2 16:13
 */
public interface JedisManager {

    /**
     * 获取Jedis对象
     *
     * @return
     */
    Jedis get();

    /**
     * 关闭
     *
     * @param jedis
     */
    void close(Jedis jedis);
}
