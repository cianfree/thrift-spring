package org.apache.thrift.test;

import org.apache.thrift.TException;

/**
 * @author Arvin
 * @time 2017/3/2 20:28
 */
public class HiServiceImpl implements HiService.Iface {
    @Override
    public String sayHi(String name) throws TException {
        return "Hi, " + name;
    }
}
