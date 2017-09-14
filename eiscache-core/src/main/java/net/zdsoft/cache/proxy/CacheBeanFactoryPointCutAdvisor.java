package net.zdsoft.cache.proxy;

import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 17-9-10下午3:47
 */
public class CacheBeanFactoryPointCutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private Logger logger = LoggerFactory.getLogger(CacheBeanFactoryPointCutAdvisor.class);

    private CacheOperationParser cacheOperationParser;

    private Pointcut pointcut = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {

            boolean isCacheInterceptor = false;
            //接口
            if ( targetClass.isInterface() ) {
                isCacheInterceptor = isTarget(targetClass, method);
            }
            //proxy
            if ( AopUtils.isJdkDynamicProxy(targetClass)
                    || AopUtils.isAopProxy(targetClass)
                    || AopUtils.isCglibProxy(targetClass) ) {
                //获取代理类的实际类型
                Class<?> realTargetClass = targetClass.getSuperclass();
                //处理实际类型和接口类型 （）
                isCacheInterceptor = isCacheInterceptor || isTarget(realTargetClass, method);
            } else {
                isCacheInterceptor = isCacheInterceptor || isTarget(targetClass, method);
            }
            if ( isCacheInterceptor ) {
                System.out.println("cacheInterceptor is Method " + method.getName() + " targetClass is " + targetClass.getName());
            }
            return isCacheInterceptor;
        }

        private boolean isTarget(Class<?> targetClass, Method method) {
            return !cacheOperationParser.parser(method, targetClass).isEmpty();
        }
    };



    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setCacheOperationParser(CacheOperationParser cacheOperationParser) {
        this.cacheOperationParser = cacheOperationParser;
    }
}
