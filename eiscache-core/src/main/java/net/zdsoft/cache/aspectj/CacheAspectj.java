package net.zdsoft.cache.aspectj;

import net.zdsoft.cache.core.Invoker;
import net.zdsoft.cache.interceptor.CacheAopExecutor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import net.zdsoft.cache.proxy.CacheBeanFactoryPointCutAdvisor;
import net.zdsoft.cache.proxy.ClassFilterAdapter;
import net.zdsoft.cache.proxy.DynamicCacheClassFilter;
import net.zdsoft.cache.proxy.TypeDescriptor;
import net.zdsoft.cache.support.ReturnTypeContext;
import net.zdsoft.cache.utils.BeanUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * cache aspectj 使用spring扫描
 * @author shenke
 * @since 2017.08.30
 */
@Aspect
class CacheAspectj extends CacheAopExecutor implements DisposableBean{

    @Autowired
    private DynamicCacheClassFilter classFilter;
    @Autowired
    private TypeDescriptor typeDescriptor;

    private CacheBeanFactoryPointCutAdvisor advisor;
    //private Environment environment;

    public CacheAspectj() {
    }

    @Override
    public void destroy() throws Exception {

    }

    //@Override
    //public void setEnvironment(Environment environment) {
    //    this.environment = environment;
    //}

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CacheOperationParser parser = new CacheOperationParser();

        this.advisor = new CacheBeanFactoryPointCutAdvisor();
        this.advisor.setClassFilter(new ClassFilterAdapter(classFilter));
        advisor.setCacheOperationParser(parser);

        //String slowCache = environment.getProperty("eiscache.slowCache");
        //String slowInvoke = environment.getProperty("eiscache.slowInvoke");

        setSlowCacheTime(Long.MAX_VALUE);
        setSlowInvokeTime(Long.MAX_VALUE);
        setCacheOperationParser(parser);
    }

    @Pointcut(value = "execution(* (@net.zdsoft.cache.annotation.CacheDefault *).*(..))")
    public void onProcessCacheMethod() {
    }


    @Around(value = "onProcessCacheMethod()")
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
            org.springframework.aop.Pointcut pointcut = advisor.getPointcut();
            Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
            boolean cacheClass = pointcut.getClassFilter().matches(targetClass);
            if ( !cacheClass ) {
                return invoker.invoke();
            }
            boolean cacheProcess = pointcut.getMethodMatcher().matches(method, targetClass, joinPoint.getArgs());
            if ( cacheProcess ) {
                if ( this.typeDescriptor == null ) {
                    ReturnTypeContext.registerReturnType(methodSignature.getReturnType());
                } else {
                    ReturnTypeContext.registerReturnType(this.typeDescriptor.buildType(new AopJoinPointAdapter(joinPoint), targetClass).returnType());
                }
                ReturnTypeContext.registerEntityType(BeanUtils.getFirstGenericType(targetClass));
                return execute(invoker, joinPoint.getTarget(), method, joinPoint.getArgs(), methodSignature.getReturnType());
            }
            return invoker.invoke();
        } catch (Invoker.ThrowableWrapper e) {
            ThrowAny.throwUnchecked(e.getOrigin());
            return null; //never
        }
    }

    private class AopJoinPointAdapter implements MethodInvocation{

        private ProceedingJoinPoint joinPoint;

        public AopJoinPointAdapter(ProceedingJoinPoint joinPoint) {
            this.joinPoint = joinPoint;
        }

        @Override
        public Method getMethod() {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            return methodSignature.getMethod();
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
            return null;
        }
    }
}
