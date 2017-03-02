package org.apache.thrift.spring.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.spring.exception.UnImplementIfaceException;

/**
 * @author Arvin
 * @time 2017/3/1 18:57
 */
public interface ProcessorProvider {

    /**
     * 创建TProcessor
     *
     * @param service 业务bean
     * @return
     * @throws UnImplementIfaceException
     */
    TProcessor provide(Object service) throws UnImplementIfaceException;
}
