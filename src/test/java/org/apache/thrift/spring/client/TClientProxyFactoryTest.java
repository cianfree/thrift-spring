package org.apache.thrift.spring.client;

import org.apache.thrift.TException;
import org.apache.thrift.test.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Arvin
 * @time 2017/3/1 21:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-thrift-client-test.xml"})
public class TClientProxyFactoryTest {

    @Autowired
    private HelloService.Iface helloService;

    @Test
    public void testClient() throws TException, InterruptedException {
        System.out.println(helloService);

        System.out.println(helloService.sayHello("Arvin"));

        Thread.sleep(500);

        System.out.println(helloService.sayHello("Cianfree"));
    }

}