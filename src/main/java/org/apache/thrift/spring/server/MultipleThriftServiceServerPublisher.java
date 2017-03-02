package org.apache.thrift.spring.server;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.spring.client.impl.DefaultProcessorProvider;
import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.server.impl.LocalServerHostTransfer;
import org.apache.thrift.spring.utils.ThriftUtils;
import org.apache.thrift.spring.utils.Utils;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 发布Thrift Service 服务, 支持多个Service发布到同一个端口
 *
 * @author Arvin
 * @time 2017/3/1 18:03
 */
public class MultipleThriftServiceServerPublisher implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MultipleThriftServiceServerPublisher.class);

    /** 发布配置 required */
    private TSConfig config;

    /** 要发布的Service列表 required */
    private List<Object> serviceList;

    /** 服务端Host提供者 required */
    private ServerHostTransfer hostTransfer;

    /** 配置上报 optional */
    private TSConfigReporter configReporter;

    /** TProcessor 提供者 optional */
    private ProcessorProvider processorProvider;

    /** 服务端线程 */
    private ServerThread serverThread;

    /** 服务列表 */
    private String serviceListString;

    public TSConfig getConfig() {
        return config;
    }

    public MultipleThriftServiceServerPublisher setConfig(TSConfig config) {
        this.config = config;
        return this;
    }

    public List<Object> getServiceList() {
        return serviceList;
    }

    public MultipleThriftServiceServerPublisher setServiceList(List<Object> serviceList) {
        // 去重
        this.serviceList = new ArrayList<>(new HashSet<>(serviceList));
        StringBuilder builder = new StringBuilder("[");
        for (Object service : this.serviceList) {
            builder.append(service.getClass().getName()).append(",");
        }
        builder.append("]");
        this.serviceListString = builder.toString().replaceAll(",\\]", "]");
        return this;
    }

    public ServerHostTransfer getHostTransfer() {
        return hostTransfer;
    }

    public MultipleThriftServiceServerPublisher setHostTransfer(ServerHostTransfer hostTransfer) {
        this.hostTransfer = hostTransfer;
        return this;
    }

    public TSConfigReporter getConfigReporter() {
        return configReporter;
    }

    public MultipleThriftServiceServerPublisher setConfigReporter(TSConfigReporter configReporter) {
        this.configReporter = configReporter;
        return this;
    }

    public ProcessorProvider getProcessorProvider() {
        return processorProvider;
    }

    public MultipleThriftServiceServerPublisher setProcessorProvider(ProcessorProvider processorProvider) {
        this.processorProvider = processorProvider;
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化默认属性
        initDefaultProperties();
        // 发布Service
        String host = this.config.getHost();
        if (Utils.isBlank(host)) {
            host = this.hostTransfer.getHost();
            this.config.setHost(host);
        }
        if (Utils.isBlank(host)) {
            throw new UnknownHostException("无法获取要发布Thrift服务的主机地址， 发布[" + serviceListString + "] 服务失败！");
        }

        // 校验参数
        validateConfig(this.config);

        // 发布多个服务
        TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
        for (Object service : this.serviceList) {
            // 以service实现的Iface所在类为ID
            String serviceId = ThriftUtils.parseServiceClass(service).getName();
            TProcessor processor = this.processorProvider.provide(service);
            multiplexedProcessor.registerProcessor(serviceId, processor);
            logger.info("注册Thrift服务： " + serviceId);
        }

        // 初始化线程
        serverThread = new ServerThread(multiplexedProcessor, this.config.getPort());
        // 发布服务
        serverThread.start();

        // 上报服务配置
        if (null != this.configReporter) {
            logger.info("上报Thrift服务【" + serviceListString + "】配置：" + TSConfig.serialize(config));
            this.configReporter.report(config);
        }
    }

    /**
     * <pre>
     * 验证参数：
     * 1. serviceId 不能为空
     * 2. host不能为空
     * 3. port必须大于0
     * </pre>
     *
     * @param config 配置项
     */
    private void validateConfig(TSConfig config) {
        if (Utils.isBlank(config.getHost())) {
            throw new IllegalArgumentException("Thrift.config.host should not be null!");
        }

        if (config.getPort() < 1) {
            throw new IllegalArgumentException("Thrift.config.port should be over 0!");
        }
    }

    private void initDefaultProperties() {
        if (this.hostTransfer == null) {
            this.hostTransfer = LocalServerHostTransfer.getDefaultInstance();
        }
        if (this.processorProvider == null) {
            this.processorProvider = DefaultProcessorProvider.getInstance();
        }
    }

    @Override
    public void destroy() throws Exception {
        stopService();
    }

    /**
     * 停止服务
     */
    private void stopService() {
        serverThread.stopServer();
        if (this.configReporter != null) {
            logger.info("移除Thrift服务【" + serviceListString + "】配置：" + TSConfig.serialize(config));
            this.configReporter.remove(this.config);
        }
        logger.info("关闭Thrift服务 [" + serviceListString + "]！");
    }

    /**
     * 服务线程
     */
    class ServerThread extends Thread {
        private TServer server;

        ServerThread(TProcessor processor, int port) throws Exception {
            TServerSocket serverTransport = new TServerSocket(port);
            TBinaryProtocol.Factory portFactory = new TBinaryProtocol.Factory(true, true);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(processor);
            args.protocolFactory(portFactory);
            server = new TThreadPoolServer(args);
        }

        @Override
        public void run() {
            try {
                logger.info("ThriftService服务 [" + MultipleThriftServiceServerPublisher.this.serviceListString + "] 已经成功发布！");
                server.serve();
            } catch (Exception e) {
                logger.warn("发布ThriftService[" + MultipleThriftServiceServerPublisher.this.serviceListString + "] 失败！");
            }
        }

        public void stopServer() {
            server.stop();
        }
    }

}
