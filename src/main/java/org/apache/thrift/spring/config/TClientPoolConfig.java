package org.apache.thrift.spring.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 定义TClient 的连接池配置
 *
 * @author Arvin
 * @time 2017/3/1 14:57
 */
public class TClientPoolConfig extends GenericObjectPoolConfig {

    public TClientPoolConfig() {
        this.setTestWhileIdle(true);
        // 连接空闲时间， 当设置了testWhileIdle的时候，连接空闲了以下时间就会去调用validateObject方法校验连接是否可用，如果不可用就会移除
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
    }
}
