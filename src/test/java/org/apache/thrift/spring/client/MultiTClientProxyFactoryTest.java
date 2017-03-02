package org.apache.thrift.spring.client;

import org.apache.thrift.TException;
import org.apache.thrift.test.HelloService;
import org.apache.thrift.test.HiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试服务器将多个服务发布到同一个端口的情况
 * @author Arvin
 * @time 2017/3/1 21:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-thrift-client-multi-test.xml"})
public class MultiTClientProxyFactoryTest {

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HelloService.Iface helloService;

    @Autowired
    private HiService.Iface hiService;

    @Test
    public void testClient() throws TException, InterruptedException {

        System.out.println(helloService);
        System.out.println(helloService.sayHello("Arvin"));

        System.out.println(hiService);
        System.out.println(hiService.sayHi("Arvin"));
    }

}