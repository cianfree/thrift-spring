package org.apache.thrift.test;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.impl.AbstractTimerTSConfigProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于内存的定时刷配置实现
 *
 * @author Arvin
 * @time 2017/3/2 14:59
 */
public class MemoryTimerTSConfigProvider extends AbstractTimerTSConfigProvider {

    public MemoryTimerTSConfigProvider() {
        // 默认5秒执行一次
        super(5000);
    }

    public MemoryTimerTSConfigProvider(int interval) {
        super(interval);
    }

    @Override
    protected List<TSConfig> syncLoadTSConfig() {
        List<TSConfig> configList = new ArrayList<>();
        configList.add(new TSConfig().setHost("localhost").setPort(9090));
        configList.add(new TSConfig().setHost("127.0.0.1").setPort(9090));
        return configList;
    }
}
