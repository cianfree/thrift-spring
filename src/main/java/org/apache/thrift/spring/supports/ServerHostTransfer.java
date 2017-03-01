package org.apache.thrift.spring.supports;

/**
 * 服务器主机提供者
 *
 * @author Arvin
 * @time 2017/3/1 18:10
 */
public interface ServerHostTransfer {

    /**
     * 获取Host， 可能是域名，ip计算机名称
     */
    String getHost();

    /**
     * 重置 host
     *
     * @param newHost 新的Host
     */
    void reset(String newHost);
}
