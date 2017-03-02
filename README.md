# Thrift集成Spring
    因thrift使用起来比较麻烦，为了能够让Spring方便的管理Thrift的客户端资源，如TSocket，TServiceClient，实际上这些资源是可以复用的，因此利用连接池的技术将这些高度利用起来，提高了利用率，提高了性能。
    
    另外，使用Spring来发布Thrift的服务或者来创建客户端代理也是非常有必要的，通过本项目将简化了Thrift的发布和使用，当然，这只是限于Java平台中的发布和使用

# 通过Spring发布Thrift服务
    通过本项目支持，可以非常轻松的进行Thrift项目的发布，具体的发布流程如下（关于Thrift.thrift的编写和相关类的生成不在本项目的说明范围之内，请读者自行阅读相关资料）
    
## 编写Spring发布配置文件
    本例中，在 `applicationContext-thrift-server-test.xml`,内容如下：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:tx="http://www.springframework.org/schema/tx"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="
    			http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
         		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd
         		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.1.xsd
         		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd">
      
        <!-- HelloService 的发布服务配置 -->
        <bean id="thriftHelloServiceConfig" class="org.apache.thrift.spring.config.TSConfig">
            <property name="host" value="127.0.0.1"/> <!-- 可选，服务的主机地址， 当然这个可以不配置，通过ServerHostTransfer进行获取 -->
            <property name="port" value="9090"/>    <!-- 必须配置，发布的端口 -->
            <property name="timeout" value="5000"/> <!-- 可选，连接本服务器的超时时间，默认是5秒 -->
        </bean>
    
        <!-- 定义要发布的业务类，实现了Thrift文件生成的java文件对应的接口 -->
        <bean id="thriftHelloService" class="org.apache.thrift.test.HelloServiceImpl"/>
    
        <!-- 使用ThriftServiceServerPublisher发布HelloService服务，一个服务对应一个端口，后期可能升级 -->
        <bean id="thriftHelloServicePublish" class="org.apache.thrift.spring.server.ThriftServiceServerPublisher">
            <property name="config" ref="thriftHelloServiceConfig"/> <!-- 指定要发布的服务的配置信息 -->
            <property name="service" ref="thriftHelloService"/> <!-- 指定要发布的服务 -->
            <!-- 可选 默认为null 配置发布者，当服务发布后会反馈本服务信息，比如注册到指定的服务等 
            <property name="configReporter" ref=""/>
            -->
            <!-- 可选 默认是org.apache.thrift.spring.supports.impl.DefaultProcessorProvider 提供TProcessor构造器
            <property name="processorProvider" ref=""/>
            -->
            <!-- 可选 默认是org.apache.thrift.spring.supports.impl.LocalServerHostTransfer 提供Host的获取
            <property name="hostTransfer" ref=""/>
            -->
        </bean>
    </beans>
    ```
## 编写测试类
    本例的测试类是：`org.apache.thrift.spring.server.ThriftServiceServerPublisherTest`
    内容如下：
    ```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration({"classpath:applicationContext-thrift-server-test.xml"})
    public class ThriftServiceServerPublisherTest {
    
       @Test
       public void testStartServer() throws Exception {
           try {
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
       }
    } 
    ```    

# 通过Spring使用Thrift客户端
    通过本项目的支持，可以非常方便的使用客户端服务，可以使用池化技术，性能良好
    
## 编写Spring客户端配置文件
    本例使用测试的配置文件`applicationContext-thrift-client-test.xml`,内容如下：
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:tx="http://www.springframework.org/schema/tx"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="
    			http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
         		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd
         		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.1.xsd
         		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd">
        
        <!-- 配置一个Thrift服务端配置的获得者，可自行实现 -->
        <bean id="configProvider" class="org.apache.thrift.test.FixedTSConfigProvider"/>
    
        <!-- 连接池配置 -->
        <bean id="poolConfig" class="org.apache.thrift.spring.config.TClientPoolConfig">
        </bean>
    
        <!-- 客户端对象 -->
        <bean id="helloService" class="org.apache.thrift.spring.client.TClientProxyFactory">
            <constructor-arg name="serviceClass" value="org.apache.thrift.test.HelloService"/> <!-- 必填 被代理的服务类或Iface接口 -->
            <constructor-arg name="configProvider" ref="configProvider"/> <!-- 必填 -->
            <property name="poolConfig" ref="poolConfig"/> <!-- 选填，如果为空就会默认新建一个默认的配置 -->
        </bean>
        
    </beans>
    ```    
## 编写测试类
    本例测试类： `org.apache.thrift.spring.client.TClientProxyFactoryTest`:
    注意，先要启动服务端，把`org.apache.thrift.spring.server.ThriftServiceServerPublisherTest.testStartServer`方法体改成 while(true);
    ```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration({"classpath:applicationContext-thrift-client-test.xml"})
    public class TClientProxyFactoryTest {
    
        // 直接注入
        @Autowired
        private HelloService.Iface helloService;
    
        @Test
        public void testClient() throws TException, InterruptedException {
            System.out.println(helloService);
    
            System.out.println(helloService.sayHello("Arvin"));
    
            Thread.sleep(2000);
    
            System.out.println(helloService.sayHello("Cianfree"));
        }
    }
    ```