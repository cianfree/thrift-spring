package org.apache.thrift.spring.exception;

/**
 * 没有实现Iface接口异常
 *
 * @author Arvin
 * @time 2017/3/1 19:29
 */
public class UnImplementIfaceException extends RuntimeException {

    public UnImplementIfaceException() {
        super("没有实现Thrift的Iface接口！");
    }

    public UnImplementIfaceException(String message) {
        super(message);
    }

    public UnImplementIfaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnImplementIfaceException(Throwable cause) {
        super(cause);
    }

    public UnImplementIfaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
