package org.apache.thrift.spring.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Arvin
 * @time 2017/3/1 20:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-thrift-server-test.xml"})
public class ThriftServiceServerPublisherTest {

    @Test
    public void testStartServer() throws InterruptedException {

        while (true) {}

        /*try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            HelloService.Client client = new HelloService.Client(protocol);

            System.out.println(client.sayHello("Arvin"));

            transport.close();

            System.out.println("RPC 请求完成！");
            Thread.sleep(2000);
        } catch (TException e) {
            e.printStackTrace();
        }
*/
    }

}