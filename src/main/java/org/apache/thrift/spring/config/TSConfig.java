package org.apache.thrift.spring.config;

/**
 * Thrift Server Config
 * <p>
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

    /** 权重，默认是1，小于0的即为1 */
    private int weight = 1;

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

    public int getWeight() {
        return weight;
    }

    public TSConfig setWeight(int weight) {
        this.weight = weight < 1 ? 1 : weight;
        return this;
    }

    /**
     * 序列化为字符串， 序列化规则为：
     * host:port:timeout:weight
     *
     * @param config 要序列话的对象
     */
    public static String serialize(TSConfig config) {
        return config.getHost() + ":" + config.getPort() + ":" + config.getTimeout() + ":" + config.getWeight();
    }

    /**
     * 反序列化，格式为：
     * host:port:timeout:weight
     *
     * @param string 要反序列化的字符串
     * @return
     */
    public static TSConfig deserialize(String string) {
        try {
            String[] arr = string.split(":");
            return new TSConfig()
                    .setHost(arr[0])
                    .setPort(Integer.parseInt(arr[1]))
                    .setTimeout(Integer.parseInt(arr[2]))
                    .setWeight(Integer.parseInt(arr[3]));
        } catch (Exception e) {
            return null;
        }
    }
}
