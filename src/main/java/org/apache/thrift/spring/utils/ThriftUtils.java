package org.apache.thrift.spring.utils;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arvin
 * @time 2017/3/2 19:04
 */
public class ThriftUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThriftUtils.class);

    private ThriftUtils() {
    }

    private static final String IFACE_REGEX_SUFFIX = "\\$Iface$";

    /**
     * 根据service解析该service对应Iface接口的全类名
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<?> parseIfaceClass(Object service) {
        return parseClass(service, "Iface");
    }

    /**
     * 从Thrift生成的Service的class中解析Iface接口的类
     *
     * @param serviceOrInnerClass 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<?> parseIfaceClassExt(String serviceOrInnerClass) {
        return parseClassExt(serviceOrInnerClass, "Iface");
    }

    /**
     * 根据service解析该service对应AsyncIface接口的全类名
     *
     * @param service 实现了Thrift服务中定义的AsyncIface的服务对象
     */
    public static Class<?> parseAsyncIfaceClass(Object service) {
        return parseClass(service, "AsyncIface");
    }

    /**
     * 从Thrift生成的Service的class中解析AsyncIface接口的类
     *
     * @param serviceOrInnerClass 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<?> parseAsyncIfaceClass(String serviceOrInnerClass) {
        return parseClassExt(serviceOrInnerClass, "AsyncIface");
    }

    /**
     * 解析Service对应的Service类， 该类是通过Thrift直接生成Java文件中的对应类
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<?> parseServiceClass(Object service) {
        return parseRootClass(service);
    }

    /**
     * 解析Service对应的Service类， 该类是通过Thrift直接生成Java文件中的对应类
     *
     * @param serviceOrInnerClassName 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<?> parseServiceClassExt(String serviceOrInnerClassName) {
        return parseRootClass(serviceOrInnerClassName);
    }

    /**
     * 解析Client的Class对象
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<TServiceClient> parseClientClass(Object service) {
        return parseClass(service, "Client");
    }

    /**
     * 解析Client的Class对象
     *
     * @param serviceOrInnerClassName 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<TServiceClient> parseClientClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "Client");
    }

    /**
     * 解析指定Service类中定义的Client.Factory类
     */
    public static Class<TServiceClientFactory<TServiceClient>> parseClientFactoryClass(Object service) {
        return parseClass(service, "Client.Factory");
    }

    /**
     * 解析指定Service类中定义的Client.Factory类
     */
    public static Class<TServiceClientFactory<TServiceClient>> parseClientFactoryClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "Client.Factory");
    }

    /**
     * 解析AsyncClient的Class对象
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<TAsyncClient> parseAsyncClientClass(Object service) {
        return parseClass(service, "AsyncClient");
    }

    /**
     * 解析AsyncClient的Class对象
     *
     * @param serviceOrInnerClassName 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<TAsyncClient> parseAsyncClientClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "AsyncClient");
    }

    /**
     * 解析指定Service类中定义的AsyncClient.Factory类
     */
    public static Class<TAsyncClientFactory<TAsyncClient>> parseAsyncClientFactoryClass(Object service) {
        return parseClass(service, "AsyncClient.Factory");
    }

    /**
     * 解析指定Service类中定义的AsyncClient.Factory类
     */
    public static Class<TAsyncClientFactory<TAsyncClient>> parseAsyncClientFactoryClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "AsyncClient.Factory");
    }

    /**
     * 解析Processor的Class对象
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<?> parseProcessorClass(Object service) {
        return parseClass(service, "Processor");
    }

    /**
     * 解析Processor的Class对象
     *
     * @param serviceOrInnerClassName 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<?> parseProcessorClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "Processor");
    }

    /**
     * 解析AsyncProcessor的Class对象
     *
     * @param service 实现了Thrift服务中定义的Iface的服务对象
     */
    public static Class<?> parseAsyncProcessorClass(Object service) {
        return parseClass(service, "AsyncProcessor");
    }

    /**
     * 解析AsyncProcessor的Class对象
     *
     * @param serviceOrInnerClassName 从Thrift生成的Service的class或该类下面的内部类
     */
    public static Class<?> parseAsyncProcessorClassExt(String serviceOrInnerClassName) {
        return parseClassExt(serviceOrInnerClassName, "AsyncProcessor");
    }

    /**
     * 解析实现了Thrift生成的类中Iface服务对象的相关类
     *
     * @param service    服务对象
     * @param expression 表达式，类和类直接使用 "." 进行分割，如要获取Iface的，那么表达式应该是.Iface, 如果是Client$Factory的，那么就是.Client.Factory
     * @param <T>        返回的类的实例
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> parseClass(Object service, String expression) {
        try {
            // 先解析根服务类
            Class<?> rootClass = parseRootClass(service);
            if (rootClass == null) {
                throw new ClassNotFoundException();
            }
            return parseClassExt(rootClass, expression);
        } catch (Exception e) {
            String objectClass = null == service ? "Unknown" : service.getClass().getName();
            String message = "找不到类，请确认类[" + objectClass + "]实现了Thrift对应业务类的Iface接口，且表达式[" + expression + "] 正确: " + e.getMessage();
            logger.warn(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * 解析内部类
     * 从Thrift生成的Service的class中解析Iface接口的类
     *
     * @param serviceOrInnerClass 从Thrift生成的Service的class或该类下面的内部类
     * @param expression          表达式，类和类直接使用 "." 进行分割，如要获取Iface的，那么表达式应该是.Iface, 如果是Client$Factory的，那么就是.Client.Factory
     * @param <T>                 返回的类的实例
     * @return
     */
    public static <T> Class<T> parseClassExt(String serviceOrInnerClass, String expression) {
        return parseClassExt(parseRootClass(serviceOrInnerClass), expression);
    }

    /**
     * 解析内部内
     *
     * @param clazz      主类
     * @param expression 表达式，类和类直接使用 "." 进行分割，如要获取Iface的，那么表达式应该是.Iface, 如果是Client$Factory的，那么就是.Client.Factory
     * @param <T>        返回的类的实例
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Class<T> parseClassExt(Class<?> clazz, String expression) {
        try {
            // 将表达式中的.替换成$
            String realExpression = expression.replaceAll("\\.", "\\$");
            if (!realExpression.startsWith("$")) {
                realExpression = "$" + realExpression;
            }
            // 构造类的全路径
            String className = clazz.getName() + realExpression;
            return (Class<T>) clazz.getClassLoader().loadClass(className);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解析根类，Iface中的外部类
     *
     * @param service 实现了Iface接口的实例对象
     */
    private static Class<?> parseRootClass(Object service) {
        try {
            Class[] interfaces = service.getClass().getInterfaces();
            Class rootClass = null;
            if (interfaces != null && interfaces.length > 0) {
                for (Class ifs : interfaces) {
                    if (ifs.getName().endsWith("$Iface")) {
                        // 找到了Thrift对应的Iface
                        String rootClassName = ifs.getName().replaceAll(IFACE_REGEX_SUFFIX, "");
                        rootClass = ifs.getClassLoader().loadClass(rootClassName);
                        break;
                    }
                }
            }
            if (rootClass == null) {
                throw new ClassNotFoundException();
            }
            return rootClass;
        } catch (Exception e) {
            String objectClass = null == service ? "Unknown" : service.getClass().getName();
            String message = "找不到类，请确认类[" + objectClass + "]实现了Thrift对应业务类的Iface接口: " + e.getMessage();
            logger.warn(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * 解析根class
     *
     * @param serviceOrInnerClass 从Thrift生成的Service的class或该类下面的内部类
     * @return
     */
    private static Class<?> parseRootClass(String serviceOrInnerClass) {
        try {
            // 把内部类的删除
            String rootClassName = serviceOrInnerClass.replaceAll("\\$.*$", "");
            return Thread.currentThread().getContextClassLoader().loadClass(rootClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
