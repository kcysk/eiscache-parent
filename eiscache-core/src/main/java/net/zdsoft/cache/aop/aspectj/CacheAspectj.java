package net.zdsoft.cache.aop.aspectj;

import net.zdsoft.cache.aop.CacheBeanFactoryPointCutAdvisor;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheAopExecutor;
import net.zdsoft.cache.core.Invoker;
import net.zdsoft.cache.support.ReturnTypeContext;
import net.zdsoft.cache.utils.BeanUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * spring 版本低于3.1 请注册该bean <br>
 * cache aspectj 使用spring扫描
 * @author shenke
 * @since 2017.08.30
 */
@Aspect
class CacheAspectj extends CacheAopExecutor implements DisposableBean{

    private CacheBeanFactoryPointCutAdvisor advisor;
    private TypeDescriptor typeDescriptor;

    public void setAdvisor(CacheBeanFactoryPointCutAdvisor advisor) {
        this.advisor = advisor;
    }

    public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }

    public CacheAspectj() {
    }

    @Override
    public void destroy() throws Exception {

    }

    @Pointcut(value = "execution(* (@net.zdsoft.cache.annotation.CacheDefault *).*(..))")
    public void onProcessCacheMethod() {
    }


    @Around(value = "onProcessCacheMethod()")
    public Object executeCacheAround(final ProceedingJoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();

        Invoker invoker = new Invoker() {
            @Override
            public Object invoke() {
                try {
                    long startTime = System.currentTimeMillis();
                    Object object = joinPoint.proceed();
                    long time = System.currentTimeMillis() - startTime;
                    if ( logger.isDebugEnabled() ) {
                        logger.debug("invoke method " + getTargetClass(joinPoint.getThis()) + "#" + method.getName() + " time is {" + time + "}ms");
                    }
                    if ( time >= slowInvokeTime && !logger.isDebugEnabled() ) {
                        logger.warn("invoke method + " + getTargetClass(joinPoint.getThis()) + "#" + method.getName() + " time is {" + time + "}ms");
                    }
                    return object;
                } catch (Throwable throwable) {
                    throw new Invoker.ThrowableWrapper(throwable);
                }
            }
        };
        try {
            org.springframework.aop.Pointcut pointcut = advisor.getPointcut();
            Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
            boolean cacheClass = pointcut.getClassFilter().matches(targetClass);
            if ( !cacheClass ) {
                return invoker.invoke();
            }
            boolean cacheProcess = pointcut.getMethodMatcher().matches(method, targetClass, joinPoint.getArgs());
            if ( cacheProcess ) {
                MethodInvocation invocation = new AopJoinPointAdapter(joinPoint);
                invocationContext.set(invocation);
                if ( this.typeDescriptor == null ) {
                    ReturnTypeContext.registerReturnType(methodSignature.getReturnType());
                } else {
                    ReturnTypeContext.registerReturnType(this.typeDescriptor.buildType(invocation, targetClass).returnType());
                }
                ReturnTypeContext.registerEntityType(BeanUtils.getFirstGenericType(targetClass));
                return execute(invoker, joinPoint.getTarget(), method, joinPoint.getArgs(), methodSignature.getReturnType());
            }
            return invoker.invoke();
        } catch (Invoker.ThrowableWrapper e) {
            ThrowAny.throwUnchecked(e.getOrigin());
            return null; //never
        } finally {
            ReturnTypeContext.removeReturnType();
            ReturnTypeContext.removeEntityType();
            invocationContext.remove();
        }
    }

    private class AopJoinPointAdapter implements MethodInvocation{

        private ProceedingJoinPoint joinPoint;
        private Method method;

        public AopJoinPointAdapter(ProceedingJoinPoint joinPoint) {
            this.joinPoint = joinPoint;
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            this.method = methodSignature.getMethod();
        }

        @Override
        public Method getMethod() {
            return this.method;
        }

        @Override
        public Object[] getArguments() {
            return this.joinPoint.getArgs();
        }

        @Override
        public Object proceed() throws Throwable {
            return this.joinPoint.proceed();
        }

        @Override
        public Object getThis() {
            return joinPoint.getThis();
        }

        @Override
        public AccessibleObject getStaticPart() {
            return this.method;
        }
    }
}
