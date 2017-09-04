package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Handler;
import net.zdsoft.cache.core.CacheInvocationContext;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheAopExecutor {

    private Handler handler;

    public Object execute(Handler.Invoker invoker, Object target, Method method, Object[] args, Class<?> returnType) {

        try {

            return invoker.invoke();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public Object execute(Handler.Invoker invoker, CacheInvocationContext invocationContext) {
        return handler.invoke(invoker, invocationContext);
    }

    protected CacheInvocationContext createInvocationContext(Object target, Method method, Object[] args, Class<?> returnType) {
        CacheInvocationContext invocationContext = new DefaultCacheInvocationContext(target, method, args, returnType, null);

        return invocationContext;
    }
}
