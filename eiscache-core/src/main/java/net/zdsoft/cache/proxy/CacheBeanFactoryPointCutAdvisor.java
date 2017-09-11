package net.zdsoft.cache.proxy;

import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author shenke
 * @since 17-9-10下午3:47
 */
public class CacheBeanFactoryPointCutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private CacheOperationParser cacheOperationParser;

    private Pointcut pointcut = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {

            boolean isCacheInterceptor = false;
            //接口
            if ( targetClass.isInterface() ) {
                isCacheInterceptor = containCacheOperation(targetClass.getDeclaredMethods(), method);
            }

            //proxy
            if ( AopUtils.isJdkDynamicProxy(targetClass)
                    || AopUtils.isAopProxy(targetClass)
                    || AopUtils.isCglibProxy(targetClass) ) {
                //获取代理类的实际类型
                Class<?> realTargetClass = targetClass.getSuperclass();
                //处理实际类型和接口类型 （）
                isCacheInterceptor = isCacheInterceptor || isTarget(realTargetClass, method);

            }

            Collection<CacheOperation> operations = cacheOperationParser.parser(method);
            isCacheInterceptor = isCacheInterceptor || isTarget(targetClass, method);
            return isCacheInterceptor;
        }

        private boolean isTarget(Class<?> targetClass, Method method) {
            boolean isCacheInterceptor = false;
            //处理实际类型
            isCacheInterceptor = containCacheOperation(targetClass.getDeclaredMethods(), method);
            //处理接口
            for (Type type : targetClass.getGenericInterfaces()) {
                if ( type instanceof ParameterizedType) {
                    Class<?> clazz = (Class<?>) ((ParameterizedType)type).getRawType();
                    if ( containCacheOperation(clazz.getDeclaredMethods(), method) ) {
                        return true;
                    }
                }else {
                    if ( containCacheOperation(((Class<?>) type).getDeclaredMethods(), method) ) {
                        return true;
                    }
                }
            }
            return isCacheInterceptor;
        }

        private boolean containCacheOperation(Method[] searchMethods, Method method) {
            for (Method tMethod : searchMethods ) {
                if ( !tMethod.getName().equals(method.getName()) ) {
                    continue;
                }
                Collection<CacheOperation> cacheOperations = cacheOperationParser.parser(tMethod);
                if ( cacheOperations != null && !cacheOperations.isEmpty() ) {
                    return true;
                }
            }
            return false;
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
