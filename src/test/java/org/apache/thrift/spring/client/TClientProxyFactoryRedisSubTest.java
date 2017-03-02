package org.apache.thrift.spring.client;

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
@ContextConfiguration({"classpath:applicationContext-thrift-client-redis-sub-test.xml"})
public class TClientProxyFactoryRedisSubTest {

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HelloService.Iface helloService;

    @Test
    public void testClient() throws Exception {

        while (true) {
            try {
                // 没两秒模拟执行一次
                Thread.sleep(5000);
                System.out.println(helloService.sayHello("Arvin"));

            } catch (Exception e) {
                System.out.println("调用错误： [" + e.getClass().getName() + "], " + e.getMessage());
            }

        }
    }

}