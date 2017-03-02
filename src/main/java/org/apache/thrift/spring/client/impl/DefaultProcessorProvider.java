package org.apache.thrift.spring.client.impl;

import org.apache.thrift.TProcessor;
import org.apache.thrift.spring.server.ProcessorProvider;
import org.apache.thrift.spring.utils.ThriftUtils;

import java.lang.reflect.Constructor;

/**
 * <pre>
 * 默认的TProcessor提供者, 构造规则如下：
 * 1. 获取给定service的Iface接口
 * 2. 根据接口获取Processor的class
 * 3. 获取第一个参数是Iface的Processor构造函数
 * 4. 实例化一个Processor
 * 5. 返回
 *
 * </pre>
 *
 * @author Arvin
 * @time 2017/3/1 19:18
 */
public class DefaultProcessorProvider implements ProcessorProvider {


    private static class Holder {
        public static final DefaultProcessorProvider INSTANCE = new DefaultProcessorProvider();
    }

    public static DefaultProcessorProvider getInstance() {
        return Holder.INSTANCE;
    }

    private DefaultProcessorProvider() {
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public TProcessor provide(Object service) {
        try {
            Class<?> iface = ThriftUtils.parseIfaceClass(service);
            // 找对应的Processor类
            Class processorClass = ThriftUtils.parseProcessorClass(service);
            // 找到只有一个参数的构造函数
            Constructor constructor = processorClass.getConstructor(iface);
            // 实例化
            Object processor = constructor.newInstance(service);
            return (TProcessor) processor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
