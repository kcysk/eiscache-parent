package net.zdsoft.cache.core;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 17-9-4下午10:28
 */
public interface CacheOperationMetaData {

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

}
