package net.zdsoft.cache.core;

import net.zdsoft.cache.Cache;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 17-9-4上午12:10
 */
public interface CacheInvocationContext  {

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Class<?> getReturnType();

    /**
     * 解析springEL表达式
     * @param springELExpression 表达式
     */
    Object evaluate(String springELExpression, Object result);

    CacheOperation getCacheOperation(Class<? extends CacheOperation> cType);

    Cache getCache();

    boolean enabledCache();
}
