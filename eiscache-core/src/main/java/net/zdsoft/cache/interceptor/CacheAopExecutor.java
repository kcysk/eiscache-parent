package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Cache;
import net.zdsoft.cache.CacheManager;
import net.zdsoft.cache.DefaultErrorHandler;
import net.zdsoft.cache.Invoker;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.core.InvocationContext;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.core.support.CacheableOperation;
import net.zdsoft.cache.expression.CacheEvaluationContext;
import net.zdsoft.cache.expression.CacheExpressionEvaluator;
import net.zdsoft.cache.listener.CacheEventListener;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheAopExecutor extends AbstractCacheInvoker implements ApplicationContextAware, BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    private BeanFactory beanFactory ;
    private CacheExpressionEvaluator evaluator = new CacheExpressionEvaluator(new SpelExpressionParser());
    private CacheOperationParser cacheOperationParser;
    private CacheErrorHanlder cacheErrorHanlder;
    private Collection<CacheEventListener> listeners;
    private CacheManager cacheManager;
    private boolean initialized = false;
    private ApplicationContext applicationContext;
    private AdviceMode activeModel;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, CacheEventListener> eventListenerMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, CacheEventListener.class, true, true);
        if ( eventListenerMap != null ) {
            listeners = eventListenerMap.values();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            this.cacheManager = beanFactory.getBean(CacheManager.class);
        } catch (Exception e) {
            throw new RuntimeException("no cacheManager");
        }
        try {
            this.cacheErrorHanlder = beanFactory.getBean(CacheErrorHanlder.class);
        } catch (Exception e){
            this.cacheErrorHanlder = new DefaultErrorHandler();
        }
        this.cacheOperationParser = new CacheOperationParser();
        this.initialized = true;
    }

    private Class<?> getTargetClass(Object target) {
        //有可能是代理类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

    public Object execute(Invoker invoker, Object target, Method method, Object[] args, Class<?> returnType) {

        try {
            if ( !this.initialized ) {
                return invoker.invoke();
            }
            Class<?> targetClass =  getTargetClass(target);
            Collection<CacheOperation> operations = this.cacheOperationParser.parser(method, AdviceMode.ASPECTJ.equals(activeModel) ? targetClass : null);
            if ( !operations.isEmpty() ) {
                CacheInvocationContexts contexts = new CacheInvocationContexts(operations, target, method, args, returnType);

                processCacheRemove(contexts.getInvocationContext(CacheRemoveOperation.class), true, CacheExpressionEvaluator.NO_RESULT);

                Object result = getFromCache(contexts.getInvocationContext(CacheableOperation.class));

                if ( result == null ) {
                    result = invoker.invoke();
                    processCachePut(contexts.getInvocationContext(CacheableOperation.class), result);
                }

                processCacheRemove(contexts.getInvocationContext(CacheRemoveOperation.class), false, result);
                return result;
            }
            return invoker.invoke();
        } catch (Throwable throwable) {
            throw new Invoker.ThrowableWrapper(throwable);
        }
    }

    protected void processCacheRemove(CacheInvocationContext invocationContext, boolean beforeInvocation, Object result) {
        if ( invocationContext == null ) {
            return ;
        }
        CacheRemoveOperation cacheRemoveOperation = (CacheRemoveOperation) invocationContext.getCacheOperation();
        Cache cache = invocationContext.getCache();
        Object key = invocationContext.generateKey(result);
        if ( key instanceof Collection) {
            doRemove(invocationContext.getCache(), ((Collection) key).toArray());
        } else if ( key instanceof Object[] ) {
            doRemove(invocationContext.getCache(), (Object[]) key);
        } else {
            doRemove(cache, key);
        }
    }

    protected Object getFromCache(CacheInvocationContext invocationContext) {
        if ( invocationContext != null ) {
            Cache cache = invocationContext.getCache();
            return doGet(cache, invocationContext.generateKey(CacheExpressionEvaluator.NO_RESULT), invocationContext.getReturnType());
        }
        return null;
    }

    protected void processCachePut(CacheInvocationContext invocationContext, Object result) {
        if ( invocationContext != null ) {
            Object key = invocationContext.generateKey(result);
            if ( key.getClass().isArray() ) {
                for (Object o : (Object[]) key) {
                    doPut(invocationContext.getCache(), o, result);
                }
            } else if ( key instanceof Collection) {
                for (Object o : (Collection) key) {
                    doPut(invocationContext.getCache(), o, result);
                }
            } else {
                doPut(invocationContext.getCache(), key, result);
            }
        }
    }

    @Override
    protected CacheErrorHanlder getCacheErrorHandler() {
        return this.cacheErrorHanlder;
    }

    @Override
    protected Collection<CacheEventListener> getCacheEventListener() {
        return this.listeners;
    }

    public void setActiveModel(AdviceMode activeModel) {
        this.activeModel = activeModel;
    }

    private Cache getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    class CacheInvocationContexts {
        private Map<Class<? extends CacheOperation>, CacheInvocationContext> invocationContextMap ;

        public CacheInvocationContexts(Collection<CacheOperation> cacheOperations, Object target, Method method, Object[] args, Class<?> returnType) {
            invocationContextMap = new HashMap<>();
            for (CacheOperation cacheOperation : cacheOperations) {
                CacheInvocationContext invocationContext = new CacheInvocationContext(target, method, args, returnType, cacheOperation);
                if ( cacheOperation instanceof CacheableOperation) {
                    invocationContextMap.put(CacheableOperation.class, invocationContext);
                }
                if ( cacheOperation instanceof  CacheRemoveOperation) {
                    invocationContextMap.put(CacheRemoveOperation.class, invocationContext);
                }
            }
        }

        public CacheInvocationContext getInvocationContext(Class<? extends CacheOperation> operationClass) {
            return invocationContextMap.get(operationClass);
        }
    }

    class CacheInvocationContext implements InvocationContext {
        private Object target;
        private Method method;
        private Object[] args;
        private Class<?> returnType;
        private Cache cache;
        private CacheOperation cacheOperation;
        public CacheInvocationContext(Object target, Method method, Object[] args, Class<?> returnType, CacheOperation cacheOperation) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.returnType = returnType;
            this.cacheOperation = cacheOperation;
            this.cache = CacheAopExecutor.this.getCache(getCacheOperation().getCacheName());
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
        public Cache getCache() {
            return this.cache;
        }

        @Override
        public CacheOperation getCacheOperation() {
            return this.cacheOperation;
        }

        @Override
        public boolean isCondition(Object result) {
            EvaluationContext context = buildContext(result);
            return evaluator.getValue(getCacheOperation().getCondition(), context, Boolean.class);
        }

        @Override
        public Object generateKey(Object result) {
            EvaluationContext context = buildContext(result);
            return evaluator.getValue(getCacheOperation().getKey(), context);
        }

        protected EvaluationContext buildContext(Object result) {
            CacheEvaluationContext context = new CacheEvaluationContext(this, getMethod(), getArgs(), evaluator.getParameterNameDiscoverer());
            if ( result == CacheExpressionEvaluator.UN_AVAILABLE ) {
                context.setUnavailable("result");
            }
            if ( result != CacheExpressionEvaluator.NO_RESULT ) {
                context.setVariable("result", result);
            }
            context.setBeanResolver(new BeanFactoryResolver(beanFactory));
            return context;
        }
    }
}
