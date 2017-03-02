package org.apache.thrift.spring.supports.impl;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.TSConfigProvider;
import org.apache.thrift.spring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 固定Thrift 服务配置提供者， 提供初始化的时候固定配置
 *
 * @author Arvin
 * @time 2017/3/2 11:05
 */
public class FixedTSConfigProvider implements TSConfigProvider {

    private static final Logger logger = LoggerFactory.getLogger(FixedTSConfigProvider.class);

    /** 保存配置列表 */
    private List<TSConfig> configList = new CopyOnWriteArrayList<>();

    /** 使用FIFO队列来选择一个配置 */
    private Queue<TSConfig> fifo = new LinkedList<>();

    /**
     * <pre>
     * 通过集合进行配置每一项都是以下格式的字符串：
     * host:port:timeout:weight
     * 参数说明：
     * host:    必填，前缀h，服务主机地址
     * port:    必填，前缀p，端口
     * timeout: 选填，前缀t，默认是5000，该值有效范围是大于0的数字，如果小于1，则记为5000
     * weight:  选填，前缀w，默认是1，该值有效范围是大于0的数字，如果小于1，则记为1
     *
     * 实例： hlocalhost:p9090:t5000:w2
     * </pre>
     *
     * @param configCol 配置列表
     */
    public FixedTSConfigProvider(Collection<String> configCol) {
        if (configCol == null || configCol.isEmpty()) {
            throw new IllegalArgumentException("没有指定Thrift服务器列表配置！");
        }
        for (String configItemStr : configCol) {
            // 通过字符串解析一个
            TSConfig config = parseTsConfig(configItemStr);
            if (config != null) {
                // weight有多少个就房多少个到list中
                for (int i = 0; i < config.getWeight(); ++i) {
                    this.configList.add(config);
                }
            }
        }
        if (this.configList.isEmpty()) {
            throw new IllegalArgumentException("没有指定Thrift服务器列表配置！");
        }
        // 打乱List顺序并添加到队列中
        Collections.shuffle(this.configList);
        // 添加到队列中
        fifo.addAll(this.configList);
    }

    /**
     * 解析配置
     *
     * @param configItemStr 配置字段
     * @return
     */
    private static TSConfig parseTsConfig(String configItemStr) {
        String[] arr = configItemStr.split(" *: *");
        TSConfig config = new TSConfig();
        for (String item : arr) {
            if (Utils.isBlank(item)) {
                continue;
            }
            if (item.matches("(?i)h.*")) { // host
                config.setHost(item.substring(1));
            }
            if (item.matches("(?i)p.*")) { // post
                config.setPort(Integer.parseInt(item.substring(1)));
            }
            if (item.matches("(?i)t.*")) { // timeout
                int timeout = Integer.parseInt(item.substring(1));
                if (timeout > 0) {
                    config.setTimeout(timeout);
                }
            }
            if (item.matches("(?i)w.*")) { // weight
                int weight = Integer.parseInt(item.substring(1));
                if (weight > 0) {
                    config.setWeight(weight);
                }
            }

        }
        return config;
    }

    @Override
    public List<TSConfig> getAll() {
        return Collections.synchronizedList(this.configList);
    }

    @Override
    public TSConfig select() {
        if (this.fifo.isEmpty()) {
            synchronized (this) {
                if (this.fifo.isEmpty()) {
                    this.fifo.addAll(this.configList);
                }
            }
        }
        return this.fifo.poll();
    }


    public static void main(String[] args) {
        parseTsConfig("h127.0.0.1:p9090:t5000:w1");
        parseTsConfig("h127.0.0.1:p9090:t-1:w1");
        parseTsConfig(":hlocalhost:t500:w2:p9090");
    }
}
