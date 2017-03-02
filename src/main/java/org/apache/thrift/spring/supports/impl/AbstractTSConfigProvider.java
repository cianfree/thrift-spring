package org.apache.thrift.spring.supports.impl;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.TSConfigProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 固定Thrift 服务配置提供者， 提供初始化的时候固定配置
 *
 * @author Arvin
 * @time 2017/3/2 11:05
 */
public abstract class AbstractTSConfigProvider implements TSConfigProvider {

    /** 保存配置列表 */
    private List<TSConfig> configList = new CopyOnWriteArrayList<>();

    /** 使用FIFO队列来选择一个配置 */
    private Queue<TSConfig> fifo = new LinkedList<>();

    public AbstractTSConfigProvider() {
    }

    /**
     * @param configList 配置列表
     */
    public AbstractTSConfigProvider(List<TSConfig> configList) {
        setConfigList(configList);
    }

    protected void setConfigList(List<TSConfig> configList) {
        if (configList == null || configList.isEmpty()) {
            throw new IllegalArgumentException("没有指定Thrift服务器列表配置！");
        }
        this.configList.addAll(configList);
        // 打乱List顺序并添加到队列中
        Collections.shuffle(this.configList);
        // 添加到队列中
        fifo.addAll(this.configList);
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
}
