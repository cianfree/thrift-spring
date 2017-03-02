package org.apache.thrift.test;

import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.client.TSConfigProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 只作为测试使用
 *
 * @author Arvin
 * @time 2017/3/1 20:54
 */
public class FixedTSConfigProvider implements TSConfigProvider {

    /** 保存Config */
    private List<TSConfig> container = new ArrayList<>();

    /**
     * 测试使用
     */
    public FixedTSConfigProvider() {
        container.add(new TSConfig().setHost("127.0.0.1").setPort(9090));
    }

    @Override
    public List<TSConfig> getAll() {
        return Collections.synchronizedList(container);
    }

    @Override
    public TSConfig select() {
        return container.get(0);
    }
}
