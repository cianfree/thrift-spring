package org.apache.thrift.spring.client;

import org.apache.thrift.spring.config.TSConfig;

import java.util.List;

/**
 * Thrift Server Config Provider
 * <p/>
 * 服务列表提供者，可以使用mysql，redis，zookeeper等实现， Reporter会定时发布服务列表，Provider就去获取，及时刷新
 *
 * @author Arvin
 * @time 2017/3/1 17:56
 */
public interface TSConfigProvider {

    /**
     * 获取所有的服务配置列表
     */
    List<TSConfig> getAll();

    /**
     * 获取一个Thrift 配置
     */
    TSConfig select();
}
