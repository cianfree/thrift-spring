package org.apache.thrift.test;

import org.apache.thrift.spring.supports.redis.JedisManager;
import redis.clients.jedis.Jedis;

/**
 * @author Arvin
 * @time 2017/3/2 16:23
 */
public class JedisManagerImpl implements JedisManager {

    private final String host;
    private final int port;

    public JedisManagerImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public Jedis get() {
        return new Jedis(host, port);
    }

    @Override
    public void close(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }
}
