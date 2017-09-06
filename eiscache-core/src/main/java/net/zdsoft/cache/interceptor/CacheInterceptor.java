package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Invoker;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author shenke
 * @since 2017.09.06
 */
public class CacheInterceptor extends CacheAopExecutor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Invoker invoker = new Invoker() {
            @Override
            public Object invoke() {
                try {
                    return invocation.proceed();
                } catch (Throwable throwable) {
                    throw new ThrowableWrapper(throwable);
                }
            }
        };
        return execute(invoker, invocation.getThis(), invocation.getMethod(), invocation.getArguments(), invocation.getMethod().getReturnType());
    }
}
