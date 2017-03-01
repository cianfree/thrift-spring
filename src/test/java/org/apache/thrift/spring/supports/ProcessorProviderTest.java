package org.apache.thrift.spring.supports;

import org.apache.thrift.TProcessor;
import org.apache.thrift.spring.supports.impl.DefaultProcessorProvider;
import org.apache.thrift.test.HelloServiceImpl;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

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