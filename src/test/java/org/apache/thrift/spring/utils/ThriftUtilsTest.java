package org.apache.thrift.spring.utils;

import org.apache.thrift.test.HelloServiceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Arvin
 * @time 2017/3/2 19:21
 */
public class ThriftUtilsTest {

    HelloServiceImpl service = new HelloServiceImpl();

    @Test
    public void testParseIfaceClass() throws Exception {
        System.out.println(ThriftUtils.parseIfaceClass(service));
    }

    @Test
    public void testParseServiceClass() throws Exception {
        System.out.println(ThriftUtils.parseServiceClass(service));
    }

    @Test
    public void testParseClientFactoryClass() throws Exception {
        System.out.println(ThriftUtils.parseClientFactoryClass(service));
    }

    @Test
    public void testParseAsyncIfaceClass() throws Exception {
        System.out.println(ThriftUtils.parseAsyncIfaceClass(service));
    }

    @Test
    public void testParseProcessorClass() throws Exception {
        System.out.println(ThriftUtils.parseProcessorClass(service));
    }

    @Test
    public void testAsyncParseProcessorClass() throws Exception {
        System.out.println(ThriftUtils.parseAsyncProcessorClass(service));
    }
}