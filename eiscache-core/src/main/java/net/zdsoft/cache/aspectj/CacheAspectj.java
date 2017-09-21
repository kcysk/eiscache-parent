package net.zdsoft.cache.aspectj;

import net.zdsoft.cache.Invoker;
import net.zdsoft.cache.interceptor.CacheAopExecutor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.Method;

/**
 * cache aspectj
 * @author shenke
 * @since 2017.08.30
 */
//@Aspect
class CacheAspectj extends CacheAopExecutor implements DisposableBean{

    public CacheAspectj() {
    }

    @Override
    public void destroy() throws Exception {

    }

    @Pointcut(value = "execution(@net.zdsoft.cache.annotation.Cacheable public * *(..))")
    public void executionAnyPublicMethodWithCacheable() {

    }

    @Pointcut(value = "execution(@net.zdsoft.cache.annotation.CacheRemove public * *(..))")
    public void executionAnyPublicMethodWithCacheClear() {

    }


    @Around(value = "executionAnyPublicMethodWithCacheable() || executionAnyPublicMethodWithCacheClear() || executeJPAQuery()")
    public Object executeCacheAround(final ProceedingJoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        Invoker invoker = new Invoker() {
            @Override
            public Object invoke() {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    throw new Invoker.ThrowableWrapper(throwable);
                }
            }
        };
        try {

            return execute(invoker, joinPoint.getTarget(), method, joinPoint.getArgs(), methodSignature.getReturnType());
        } catch (Invoker.ThrowableWrapper e) {
            ThrowAny.throwUnchecked(e.getOrigin());
            return null; //never
        }
    }


    //jpa dao pointcut

    @Pointcut(value = "execution(@net.zdsoft.cache.annotation.Cacheable public * org.springframework.data.repository.Repository+.*(..)))")
    public void executeJPAQuery() {

    }

}
