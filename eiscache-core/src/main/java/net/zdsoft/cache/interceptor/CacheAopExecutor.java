package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Cache;
import net.zdsoft.cache.Invoker;
import net.zdsoft.cache.core.CacheResolver;
import net.zdsoft.cache.core.InvocationContext;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.expression.CacheEvaluationContext;
import net.zdsoft.cache.expression.CacheExpressionEvaluator;
import net.zdsoft.cache.listener.CacheEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheAopExecutor extends AbstractCacheInvoker implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    private BeanFactory beanFactory ;
    private CacheExpressionEvaluator evaluator = new CacheExpressionEvaluator(new SpelExpressionParser());
    private CacheOperationParser cacheOperationParser;
    private CacheResolver cacheResolver;
    private CacheErrorHanlder cacheErrorHanlder;
    private Collection<CacheEventListener> listeners;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if ( cacheOperationParser == null ) {
            cacheOperationParser = new CacheOperationParser();
        }
        this.cacheResolver = beanFactory.getBean(CacheResolver.class);
    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    public Object execute(Invoker invoker, Object target, Method method, Object[] args, Class<?> returnType) {

        try {

            return invoker.invoke();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    protected InvocationContext createInvocationContext(Object target, Method method, Object[] args, Class<?> returnType) {
        CacheInvocationContext invocationContext = new CacheInvocationContext(target, method, args, returnType);

        return invocationContext;
    }

    protected void processCacheRemove(CacheInvocationContext invocationContext, boolean beforeInvocation) {
        CacheRemoveOperation cacheRemoveOperation = (CacheRemoveOperation) invocationContext.getCacheOperation();
        Cache cache = invocationContext.getCache(cacheRemoveOperation.getCacheName());
        doRemove(cache, invocationContext.generateKey(CacheExpressionEvaluator.NO_RESULT));
    }

    @Override
    protected CacheErrorHanlder getCacheErrorHandler() {
        return null;
    }

    @Override
    protected Collection<CacheEventListener> getCacheEventListener() {
        return null;
    }

    class CacheInvocationContext implements InvocationContext {
        private Object target;
        private Method method;
        private Object[] args;
        private Class<?> returnType;
        private Map<Class<? extends CacheOperation>, CacheOperation> classCacheOperationMap;

        public CacheInvocationContext(Object target, Method method, Object[] args, Class<?> returnType) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.returnType = returnType;
            Collection<CacheOperation> cacheOperations = cacheOperationParser.parser(method);
            classCacheOperationMap = new HashMap<>(cacheOperations.size());
            for (CacheOperation cacheOperation : cacheOperations) {
                classCacheOperationMap.put(cacheOperation.getClass(), cacheOperation);
            }
        }

        @Override
        public Object getTarget() {
            return this.target;
        }

        @Override
        public Method getMethod() {
            return this.method;
        }

        @Override
        public Object[] getArgs() {
            return this.args;
        }

        @Override
        public Class<?> getReturnType() {
            return this.returnType;
        }

        @Override
        public Cache getCache(String cacheName) {
            return cacheResolver.resolver(cacheName);
        }

        @Override
        public CacheOperation getCacheOperation() {
            return null;
        }

        @Override
        public boolean isCondition(Object result) {
            return false;
        }

        @Override
        public Object generateKey(Object result) {
            return null;
        }

        public Object evaluate(String expression, Object result) {

            CacheEvaluationContext context = new CacheEvaluationContext(this, getMethod(), getArgs(), evaluator.getParameterNameDiscoverer());
            if ( result == CacheExpressionEvaluator.UN_AVAILABLE ) {
                context.setUnavailable("result");
            }
            if ( result != CacheExpressionEvaluator.NO_RESULT ) {
                context.setVariable("result", result);
            }
            context.setBeanResolver(new BeanFactoryResolver(beanFactory));
            return evaluator.getValue(expression, context);
        }

        public CacheOperation getCacheOperation(Class<? extends CacheOperation> cType) {
            return classCacheOperationMap.get(cType);
        }
    }
}
