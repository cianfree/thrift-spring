package org.apache.thrift.spring.supports;

import org.apache.thrift.TProcessor;
import org.apache.thrift.spring.server.ProcessorProvider;
import org.apache.thrift.spring.client.impl.DefaultProcessorProvider;
import org.apache.thrift.test.HelloServiceImpl;
import org.junit.Test;

/**
 * @author Arvin
 * @time 2017/3/1 19:00
 */
public class ProcessorProviderTest {

    /**
     * 生成默认的Processor
     */
    @Test
    public void testGenDefaultProcessor() throws Exception {

        ProcessorProvider processorProvider = DefaultProcessorProvider.getInstance();

        TProcessor processor = processorProvider.provide(new HelloServiceImpl());

        System.out.println(processor);
    }

}