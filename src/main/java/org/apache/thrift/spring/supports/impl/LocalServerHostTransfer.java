package org.apache.thrift.spring.supports.impl;

import org.apache.thrift.spring.supports.ServerHostTransfer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 计算本地服务地址
 * @author Arvin
 * @time 2017/3/1 18:13
 */
public class LocalServerHostTransfer implements ServerHostTransfer {

    private static class Holder {
        public static final LocalServerHostTransfer DefaultInstance = new LocalServerHostTransfer();
    }

    public static LocalServerHostTransfer getDefaultInstance() {
        return Holder.DefaultInstance;
    }

    private String cacheHost = null;

    public LocalServerHostTransfer setHost(String host) {
        this.cacheHost = host;
        return this;
    }

    @Override
    public String getHost() {
        if (null != cacheHost) {
            return cacheHost;
        }
        try {
            // 一个主机有多个网络接口
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = netInterfaces.nextElement();
                //每个网络接口,都会有多个"网络地址",比如一定会有lookback地址,会有siteLocal地址等.以及IPV4或者IPV6    .
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
                        this.cacheHost = address.getHostAddress();
                        return this.cacheHost;
                    }
                }
            }
        } catch (SocketException e) {
            this.cacheHost = "localhost";
        }
        return this.cacheHost;
    }

    @Override
    public void reset(String newHost) {
        this.cacheHost = newHost;
    }
}
