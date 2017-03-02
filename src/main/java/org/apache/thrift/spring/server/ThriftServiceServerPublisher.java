package org.apache.thrift.spring.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.supports.ProcessorProvider;
import org.apache.thrift.spring.supports.ServerHostTransfer;
import org.apache.thrift.spring.supports.TSConfigReporter;
import org.apache.thrift.spring.supports.impl.DefaultProcessorProvider;
import org.apache.thrift.spring.supports.impl.LocalServerHostTransfer;
import org.apache.thrift.spring.utils.Utils;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.UnknownHostException;

/**
 * 发布Thrift Service 服务
 *
 * @author Arvin
 * @time 2017/3/1 18:03
 */
public class ThriftServiceServerPublisher implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ThriftServiceServerPublisher.class);

    /** 发布配置 required */
    private TSConfig config;

    /** 要发布的Service required */
    private Object service;

    /** 服务端Host提供者 required */
    private ServerHostTransfer hostTransfer;

    /** 配置上报 optional */
    private TSConfigReporter configReporter;

    /** TProcessor 提供者 optional */
    private ProcessorProvider processorProvider;

    /** 服务端线程 */
    private ServerThread serverThread;

    public TSConfig getConfig() {
        return config;
    }

    public ThriftServiceServerPublisher setConfig(TSConfig config) {
        this.config = config;
        return this;
    }

    public Object getService() {
        return service;
    }

    public ThriftServiceServerPublisher setService(Object service) {
        this.service = service;
        return this;
    }

    public ServerHostTransfer getHostTransfer() {
        return hostTransfer;
    }

    public ThriftServiceServerPublisher setHostTransfer(ServerHostTransfer hostTransfer) {
        this.hostTransfer = hostTransfer;
        return this;
    }

    public TSConfigReporter getConfigReporter() {
        return configReporter;
    }

    public ThriftServiceServerPublisher setConfigReporter(TSConfigReporter configReporter) {
        this.configReporter = configReporter;
        return this;
    }

    public ProcessorProvider getProcessorProvider() {
        return processorProvider;
    }

    public ThriftServiceServerPublisher setProcessorProvider(ProcessorProvider processorProvider) {
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
            throw new UnknownHostException("无法获取要发布Thrift服务的主机地址， 发布[" + service.getClass().getName() + "] 服务失败！");
        }

        // 校验参数
        validateConfig(this.config);

        // 获取Processor
        TProcessor processor = this.processorProvider.provide(service);
        // 初始化线程
        serverThread = new ServerThread(processor, this.config.getPort());
        // 发布服务
        serverThread.start();

        // 上报服务配置
        if (null != this.configReporter) {
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
            this.configReporter.remove(this.config);
        }
        logger.info("关闭Thrift服务 [" + this.service.getClass().getName() + "]！");
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
                logger.info("ThriftService服务 [" + ThriftServiceServerPublisher.this.service.getClass().getName() + "] 已经成功发布！");
                server.serve();
            } catch (Exception e) {
                logger.warn("发布ThriftService[" + ThriftServiceServerPublisher.this.service.getClass().getName() + "] 失败！");
            }
        }

        public void stopServer() {
            server.stop();
        }
    }

}
