package net.zdsoft.cache.proxy;

import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.interceptor.CacheInterceptor;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author shenke
 * @since 2017.09.06
 */
@Configuration
public class CacheProxyConfiguration {

    @Bean(name = "org.zdsoft.cache.proxy.internalCacheAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor() {
        CacheOperationParser cacheOperationParser = cacheOperationParser();
        AbstractBeanFactoryPointcutAdvisor advisor = new AbstractBeanFactoryPointcutAdvisor() {

            private Pointcut pointcut = new StaticMethodMatcherPointcut() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    Type[] types = targetClass.getGenericInterfaces();
                    if ( AopUtils.isJdkDynamicProxy(targetClass)
                            || AopUtils.isAopProxy(targetClass)
                            || AopUtils.isCglibProxy(targetClass) ){
                        Class<?> realTargetClass = targetClass.getSuperclass();
                        return isTarget(realTargetClass, method);
                    } else {
                        if ( targetClass.isInterface() ) {
                            for (Method m : targetClass.getDeclaredMethods()) {
                                Collection<CacheOperation> cacheOperations = cacheOperationParser.parser(method);
                                if ( cacheOperations != null && !cacheOperations.isEmpty()) {
                                    return true;
                                }
                            }
                        }
                        return isTarget(targetClass, method);
                    }
                }

                private boolean isTarget(Class<?> targetClass, Method method) {
                    for (Type type : targetClass.getGenericInterfaces()) {
                        if ( type instanceof ParameterizedType ) {
                            Class<?> clazz = (Class<?>) ((ParameterizedType)type).getRawType();
                            for (Method method1 : clazz.getDeclaredMethods()) {
                                if ( method1.getName().equals(method.getName()) ) {
                                    Collection<CacheOperation> operations = cacheOperationParser.parser(method1);
                                    if ( operations != null && !operations.isEmpty() ) {
                                        return true;
                                    }
                                }
                            }
                        }else {
                            for (Method imethod : ((Class<?>) type).getDeclaredMethods()) {
                                if (imethod.getName().equals(method.getName())) {
                                    Collection<CacheOperation> operations = cacheOperationParser.parser(imethod);
                                    if ( operations != null && !operations.isEmpty() ) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
            };

            @Override
            public Pointcut getPointcut() {
                return pointcut;
            }
        };
        CacheInterceptor interceptor = cacheInterceptor();
        interceptor.setActiveModel(AdviceMode.PROXY);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Bean
    public CacheInterceptor cacheInterceptor(){
        return new CacheInterceptor();
    }

    @Bean
    public CacheOperationParser cacheOperationParser() {
        return new CacheOperationParser();
    }
}
