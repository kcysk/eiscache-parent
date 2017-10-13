package net.zdsoft.cache.aop.proxy;

import net.zdsoft.cache.aop.interceptor.CacheAopExecutor;
import net.zdsoft.cache.utils.BeanUtils;
import net.zdsoft.cache.core.Invoker;
import net.zdsoft.cache.support.ReturnTypeContext;
import net.zdsoft.cache.aop.TypeDescriptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author shenke
 * @since 2017.09.06
 */
public class CacheInterceptor extends CacheAopExecutor implements MethodInterceptor {

    private TypeDescriptor typeDescriptor;

    @Override
    public Object invoke( final MethodInvocation invocation) throws Throwable {
        Invoker invoker = new Invoker() {
            @Override
            public Object invoke() {
                long start = System.currentTimeMillis();
                try {
                    Object obj = invocation.proceed();
                    long time = System.currentTimeMillis() - start;
                    if ( logger.isDebugEnabled() ) {
                        logger.debug("invoke method " + getTargetClass(invocation.getThis()) + "#" + invocation.getMethod().getName() + " time is {" + time + "}ms");
                    }
                    if ( time >= slowInvokeTime && !logger.isDebugEnabled() ) {
                        logger.warn("invoke method " + getTargetClass(invocation.getThis()) + "#" + invocation.getMethod().getName() + " time is {" + time + "}ms");
                    }
                    return obj;
                } catch (Throwable throwable) {
                    throw new ThrowableWrapper(throwable);
                }
            }
        };
        //当使用基类和泛型的时候，无法获取真正的返回值类型
        Class<?> targetClass = getTargetClass(invocation.getThis());
        if ( this.typeDescriptor == null ) {
            ReturnTypeContext.registerReturnType(invocation.getMethod().getGenericReturnType());
        } else {
            ReturnTypeContext.registerReturnType(this.typeDescriptor.buildType(invocation, targetClass).returnType());
        }
        ReturnTypeContext.registerEntityType(BeanUtils.getFirstGenericType(targetClass));
        invocationContext.set(invocation);
        try {
            return execute(invoker, invocation.getThis(), invocation.getMethod(), invocation.getArguments(), invocation.getMethod().getReturnType());
        } finally {
            invocationContext.remove();
            ReturnTypeContext.removeEntityType();
            ReturnTypeContext.removeReturnType();
        }
    }

    public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }
}
