package net.zdsoft.cache.monitor;

import java.util.List;

/**
 * @author shenke
 * @since 2017.10.13
 */
public interface CacheTargetMBean {

    List<String> getCacheClasses();

    List<String> getCacheMethods(String className);

    List<String> getBlackListOfClass();

    List<String> getBlackListOfMethod(String className);

    void addClassBlackList(String className);

    /**
     * @param methodName ex:<code>net.zdsoft.cache.monitor.CacheTargetMBean#getBlackListOfClass</code>
     */
    void addMethodBlackList(String methodName);

    void removeClassBlackList(String className);

    /**
     * @param methodName ex:<code>net.zdsoft.cache.monitor.CacheTargetMBean#getBlackListOfClass</code>
     */
    void removeMethodBlack(String methodName);
}
