package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.utils.BeanUtils;
import net.zdsoft.cache.Invoker;
import net.zdsoft.cache.support.ReturnTypeContext;
import net.zdsoft.cache.proxy.TypeDescriptor;
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
                try {
                    return invocation.proceed();
                } catch (Throwable throwable) {
                    throw new ThrowableWrapper(throwable);
                }
            }
        };
        //当使用基类和泛型的时候，
        Class<?> targetClass = getTargetClass(invocation.getThis());
        if ( this.typeDescriptor == null ) {
            ReturnTypeContext.registerReturnType(invocation.getMethod().getGenericReturnType());
        } else {
            ReturnTypeContext.registerReturnType(this.typeDescriptor.buildType(invocation, targetClass).returnType());
        }
        ReturnTypeContext.registerEntityType(BeanUtils.getFirstGenericType(targetClass));
        return execute(invoker, invocation.getThis(), invocation.getMethod(), invocation.getArguments(), invocation.getMethod().getReturnType());
    }

    public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }
}
