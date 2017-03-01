package org.apache.thrift.test;

import org.apache.thrift.TException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author Arvin
 * @time 2017/3/1 19:06
 */
@Component("thriftHelloService")
public class HelloServiceImpl implements HelloService.Iface,InitializingBean {
    @Override
    public String sayHello(String name) throws TException {
        return "Hello, " + name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
