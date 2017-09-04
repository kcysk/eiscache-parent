package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheInvocationContext;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheAopExecutor {

    public Object execute(Invoker invoker, Object target, Method method, Object[] args, Class<?> returnType) {

        return invoker.invoke();
    }

    protected CacheInvocationContext createInvocationContext(Object target, Method method, Object[] args) {
        CacheInvocationContext invocationContext = new DefaultCacheInvocationContext(target, method, args);
        //invocationContext.evaluate(
        return invocationContext;
    }

}
