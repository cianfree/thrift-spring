package org.apache.thrift.spring.client;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.spring.config.TClientPoolConfig;
import org.apache.thrift.spring.utils.ThriftUtils;
import org.apache.thrift.spring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 根据接口创建代理实现类并返回对象
 *
 * @author Arvin
 * @time 2017/3/1 20:32
 */
public class TClientProxyFactory implements FactoryBean, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TClientProxyFactory.class);

    /** 代理对象实例 */
    private Object proxyObject;

    /** 对象类型 */
    private Class<?> objectType;

    /** TClient连接池 */
    private GenericObjectPool<TServiceClient> pool;

    /** 服务类，可以是由Thrift 生成出来的 那个类，也可以是Iface的全类名，系统会自动适配 */
    private final String serviceClass;

    /** 连接池配置信息 */
    private TClientPoolConfig poolConfig;

    /** 服务器提供者配置 */
    private final TSConfigProvider configProvider;

    /** 是否使用严格模式，严格模式下，客户端需要提供ServiceId才能获取客户端，ServiceId通过serviceClass计算，不包含Iface */
    private boolean strictMode;

    /** 服务唯一ID， 可以自定义 */
    private String serviceId;

    public TClientProxyFactory(String serviceClass, TSConfigProvider configProvider) {
        this.serviceClass = serviceClass;
        this.configProvider = configProvider;
    }

    public String getServiceId() {
        return serviceId;
    }

    public TClientProxyFactory setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public TClientProxyFactory setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return this;
    }

    public TClientPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public TClientProxyFactory setPoolConfig(TClientPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    @Override
    public Object getObject() throws Exception {
        return this.proxyObject;
    }

    @Override
    public Class<?> getObjectType() {
        return this.objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // 初始化服务ID
            initServiceId();

            if (this.poolConfig == null) {
                logger.info("使用默认的连接池配置【" + this.serviceClass + "】");
                this.poolConfig = new TClientPoolConfig();
            }

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 加载Iface接口
            this.objectType = ThriftUtils.parseIfaceClassExt(serviceClass);
            logger.info("Load Iface type successfully [" + this.objectType.getName() + "]");

            // 加载Client.Factory类
            Class<TServiceClientFactory<TServiceClient>> fi = ThriftUtils.parseClientFactoryClassExt(serviceClass);
            TServiceClientFactory<TServiceClient> clientFactory = fi.newInstance();

            TClientPooledObjectFactory clientPool = null;
            if (strictMode) { // 严格模式下，需要指定serviceId
                clientPool = new TClientPooledObjectFactory(configProvider, clientFactory, serviceId);
            } else {
                clientPool = new TClientPooledObjectFactory(configProvider, clientFactory);
            }

            pool = new GenericObjectPool<>(clientPool, poolConfig);

            // 创建动态代理
            this.proxyObject = Proxy.newProxyInstance(classLoader, new Class[]{this.objectType}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // 从连接池中获取client
                    TServiceClient client = pool.borrowObject();
                    try {
                        // 执行方法
                        return method.invoke(client, args);
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        // 释放到连接池中
                        pool.returnObject(client);
                    }
                }
            });
            logger.info("Build[" + this.objectType.getName() + "]'s proxy successfully!");
        } catch (Exception e) {
            logger.error("Create Thrift Client [" + this.serviceClass + "] Proxy Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 初始化服务ID， 如果用户设置了就用户的，否则根据serviceClass计算
     */
    private void initServiceId() {
        if (Utils.isBlank(this.serviceId)) {
            this.serviceId = ThriftUtils.parseServiceClassExt(serviceClass).getName();
        }
    }

    /**
     * 加载Iface接口
     *
     * @param serviceClass 业务类全路径，有可能是Thrift生成的类，也可能直接是Iface接口
     * @return
     */
    private Class<?> loadObjectType(String serviceClass) throws ClassNotFoundException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (serviceClass.endsWith("$Iface")) {
                return classLoader.loadClass(serviceClass);
            }
            serviceClass = serviceClass + "$Iface";
            return classLoader.loadClass(serviceClass);
        } catch (ClassNotFoundException e) {
            logger.error("Load thrift service interface error: " + e.getMessage());
            throw e;
        }
    }
}
