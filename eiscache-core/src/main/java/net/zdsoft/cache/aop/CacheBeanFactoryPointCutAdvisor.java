/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.zdsoft.cache.aop;

import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import net.zdsoft.cache.support.MethodClassKey;
import org.apache.log4j.Logger;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 17-9-10下午3:47
 */
public class CacheBeanFactoryPointCutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private Logger logger = Logger.getLogger(CacheBeanFactoryPointCutAdvisor.class);

    private CacheOperationParser cacheOperationParser;
    private Map<MethodClassKey, String> isInterceptorMap = new ConcurrentHashMap<MethodClassKey, String>();

    public static final String UN_KNOWN = "un_known";
    private static final String INTERCEPT_YES = "interceptor_yes";
    private static final String INTERCEPT_NO = "interceptor_no";

    private Pointcut pointcut = new CachePointCut();
    private ClassFilter classFilter;

    public CacheBeanFactoryPointCutAdvisor() {

    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setCacheOperationParser(CacheOperationParser cacheOperationParser) {
        this.cacheOperationParser = cacheOperationParser;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }

    private boolean isTarget(Class<?> targetClass, Method method) {
        return !cacheOperationParser.parser(method, targetClass).isEmpty();
    }

    private class CachePointCut extends DynamicMethodMatcherPointcut {
        @Override
        public boolean matches(Method method, Class<?> targetClass, Object[] args) {
            long matchStart = System.currentTimeMillis();
            boolean isCacheInterceptor = false;
            MethodClassKey key = new MethodClassKey(method, targetClass);
            String inteceptor = isInterceptorMap.get(key);
            if ( INTERCEPT_YES.equals(inteceptor) ) {
                return true;
            } else if ( INTERCEPT_NO.equals(inteceptor) ) {
                return false;
            }

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
            isInterceptorMap.put(key, isCacheInterceptor ? INTERCEPT_YES : INTERCEPT_NO);
            if ( logger.isDebugEnabled() ) {
                logger.debug("process parse is interceptor method "+
                        targetClass.getName()+"#" + method.getName() + " time= " +(System.currentTimeMillis() - matchStart)+ "ms");
            }
            return isCacheInterceptor;
        }

        @Override
        public ClassFilter getClassFilter() {
            return CacheBeanFactoryPointCutAdvisor.this.classFilter;
        }
    }
}
