package org.apache.thrift.spring.client;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.spring.config.TSConfig;
import org.apache.thrift.spring.utils.Utils;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管理连接池中的TServiceClient对象
 *
 * @author Arvin
 * @time 2017/3/1 20:16
 */
public class TClientPooledObjectFactory extends BasePooledObjectFactory<TServiceClient> {

    private static final Logger logger = LoggerFactory.getLogger(TClientPooledObjectFactory.class);

    /** 配置提供者 */
    private final TSConfigProvider configProvider;

    /** 客户端的工厂类 */
    private final TServiceClientFactory<TServiceClient> clientFactory;

    /** ServiceId */
    private String serviceId;

    public TClientPooledObjectFactory(TSConfigProvider configProvider, TServiceClientFactory<TServiceClient> clientFactory) {
        this.configProvider = configProvider;
        this.clientFactory = clientFactory;
    }

    public TClientPooledObjectFactory(TSConfigProvider configProvider, TServiceClientFactory<TServiceClient> clientFactory, String serviceId) {
        this.configProvider = configProvider;
        this.clientFactory = clientFactory;
        this.serviceId = serviceId;
    }

    /**
     * 创建一个对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public TServiceClient create() throws Exception {
        // 选择一个服务器配置
        TSConfig config = this.configProvider.select();

        if (Utils.isBlank(this.serviceId)) {
            return createWithoutServiceId(config);
        } else {
            return createWithServiceId(config);
        }
    }

    /**
     * 根据ServiceId获取， 适用于服务端多个服务发布到同一个端口的情况
     *
     * @param config 配置
     * @return
     */
    private TServiceClient createWithServiceId(TSConfig config) throws TTransportException {
        TSocket transport = new TSocket(config.getHost(), config.getPort());
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        // 指定serviceId
        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, this.serviceId);

        TServiceClient client = this.clientFactory.getClient(multiplexedProtocol);

        logger.debug("创建一个ServiceId为【" + this.serviceId + "】的新TServiceClient： " + client);
        return client;
    }

    /**
     * 不需要ServiceId， 适用于服务端同一个端口只发布一个服务实例的情况
     *
     * @param config 配置
     * @return
     * @throws TTransportException
     */
    private TServiceClient createWithoutServiceId(TSConfig config) throws TTransportException {
        TSocket socket = new TSocket(config.getHost(), config.getPort());

        if (config.getTimeout() > 0) {
            socket.setTimeout(config.getTimeout());
        }

        TProtocol protocol = new TBinaryProtocol(socket);

        TServiceClient client = this.clientFactory.getClient(protocol);

        socket.open();

        logger.debug("创建一个新的TServiceClient： " + client);
        return client;
    }

    @Override
    public PooledObject<TServiceClient> wrap(TServiceClient client) {
        return new DefaultPooledObject<>(client);
    }

    /**
     * 从连接池中删除该对象
     *
     * @param pooledObject 要删除的对象
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<TServiceClient> pooledObject) throws Exception {
        logger.debug("销毁一个TServiceClient： " + pooledObject.getObject());
        super.destroyObject(pooledObject);
        TTransport transport = pooledObject.getObject().getInputProtocol().getTransport();
        if (transport != null) {
            transport.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<TServiceClient> pooledObject) {
        logger.debug("Validate 一个 TServiceClient 状态： " + pooledObject.getObject());
        TTransport tansport = pooledObject.getObject().getInputProtocol().getTransport();
        return tansport.isOpen();
    }

    public TSConfigProvider getConfigProvider() {
        return configProvider;
    }

    public TServiceClientFactory<TServiceClient> getClientFactory() {
        return clientFactory;
    }
}
