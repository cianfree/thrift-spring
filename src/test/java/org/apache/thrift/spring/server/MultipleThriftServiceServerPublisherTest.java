package org.apache.thrift.spring.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.test.HelloService;
import org.apache.thrift.test.HiService;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 同时发布多个Thrift服务同一个端口上
 *
 * @author Arvin
 * @time 2017/3/2 20:26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-thrift-server-multi-test.xml"})
public class MultipleThriftServiceServerPublisherTest {

    @Test
    public void testStartMultiServer() {
        System.out.println("启动成功。。。");

        testClient();

        while (true) {

        }
    }

    /**
     * 测试客户端调用
     */
    private void testClient() {
        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();


            TProtocol protocol = new TBinaryProtocol(transport);

            TMultiplexedProtocol mp1 = new TMultiplexedProtocol(protocol, HiService.class.getName());
            HiService.Client client1 = new HiService.Client(mp1);
            System.out.println("Client1： " + client1.sayHi("Arvin"));


            TMultiplexedProtocol mp2 = new TMultiplexedProtocol(protocol, HelloService.class.getName());
            HelloService.Client client2 = new HelloService.Client(mp2);

            System.out.println("Client2: " + client2.sayHello("Arvin"));

            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

}