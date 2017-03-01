package org.apache.thrift.spring.utils;

/**
 * @author Arvin
 * @time 2017/3/1 19:42
 */
public class Utils {

    public static boolean isBlank(String value) {
        return null == value || "".equals(value.trim());
    }

    public static boolean isNotBlank(String value) {
        return null != value && !"".equals(value.trim());
    }
}
