package org.apache.thrift.spring.client.impl;

import org.apache.thrift.TProcessor;
import org.apache.thrift.spring.exception.UnImplementIfaceException;
import org.apache.thrift.spring.server.ProcessorProvider;

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
    public TProcessor provide(Object service) {
        try {
            Class[] interfaces = service.getClass().getInterfaces();
            Class iface = null;
            if (interfaces != null && interfaces.length > 0) {
                for (Class ifs : interfaces) {
                    if (ifs.getName().endsWith("$Iface")) {
                        iface = ifs; // 找到了Thrift对应的Iface
                        break;
                    }
                }
            }
            if (null == iface) {
                throw new UnImplementIfaceException(service.getClass().getName() + " 没有实现Thrift的Iface接口!");
            }
            // 找对应的Processor类
            String processClassName = iface.getName().replaceAll("Iface$", "Processor");
            Class processorClass = iface.getClassLoader().loadClass(processClassName);
            // 找到只有一个参数的构造函数
            Constructor constructor = processorClass.getConstructor(iface);
            // 实例化
            Object processor = constructor.newInstance(service);
            return (TProcessor) processor;
        } catch (UnImplementIfaceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
