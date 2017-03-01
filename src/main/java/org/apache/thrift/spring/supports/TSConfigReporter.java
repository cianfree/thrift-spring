package org.apache.thrift.spring.supports;

import org.apache.thrift.spring.config.TSConfig;

/**
 * Thrift Server Config Reporter
 * <p/>
 * Thrift 服务端配置报告
 * <p/>
 * 比如报告到一个中央地区，如mysql，redis，zookeeper等，然后客户端及时进行更新这些服务端的配置信息
 *
 * @author Arvin
 * @time 2017/3/1 17:36
 */
public interface TSConfigReporter {

    /**
     * 上报服务注册信息
     *
     * @param tsConfig thrift server 配置信息
     */
    void report(TSConfig tsConfig);

    /**
     * 移除配置
     *
     * @param tsConfig 要移除的配置
     */
    void remove(TSConfig tsConfig);
}
