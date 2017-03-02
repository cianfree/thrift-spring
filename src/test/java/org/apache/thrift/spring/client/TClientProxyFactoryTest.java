package org.apache.thrift.spring.client;

import org.apache.thrift.TException;
import org.apache.thrift.test.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HelloService.Iface helloService;

    @Test
    public void testClient() throws TException, InterruptedException {
        System.out.println(helloService);

        System.out.println(helloService.sayHello("Arvin"));

        Thread.sleep(2000);

        System.out.println(helloService.sayHello("Cianfree"));

        System.out.println("Logger: " + logger);

        logger.debug("Debug info...");
        logger.info("Info info...");
        logger.warn("Warn info...");
        logger.error("Error info...");
        logger.trace("Trace info...");

        while (true) {

        }
    }

}