package org.apache.thrift.spring.config;

/**
 * Thrift Server Config
 * <p/>
 * Thrift 服务端配置
 *
 * @author Arvin
 * @time 2017/3/1 17:44
 */
public class TSConfig {

    /** 服务主机 */
    private String host;

    /** 提供服务的端口 */
    private int port;

    /** 超时时间，单位是毫秒， 默认5秒超时 */
    private int timeout = 5000;

    public String getHost() {
        return host;
    }

    public TSConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public TSConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public TSConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
