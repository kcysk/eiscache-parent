package net.zdsoft.cache.aop.interceptor;

import net.zdsoft.cache.core.Cache;
import net.zdsoft.cache.core.CacheManager;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.core.InvocationContext;
import net.zdsoft.cache.core.Invoker;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.core.support.CacheableOperation;
import net.zdsoft.cache.expression.CacheEvaluationContext;
import net.zdsoft.cache.expression.CacheExpressionEvaluator;
import net.zdsoft.cache.listener.CacheEventListener;
import net.zdsoft.cache.support.DefaultErrorHandler;
import net.zdsoft.cache.support.MethodClassKey;
import net.zdsoft.cache.utils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke
 * @since 2017.09.04
 */
public abstract class CacheAopExecutor extends AbstractCacheInvoker implements ApplicationContextAware, BeanFactoryAware, InitializingBean {

    protected Logger logger = Logger.getLogger(CacheAopExecutor.class);

    private BeanFactory beanFactory ;
    private CacheExpressionEvaluator evaluator = new CacheExpressionEvaluator(new SpelExpressionParser());
    private CacheOperationParser cacheOperationParser;
    private CacheErrorHanlder cacheErrorHanlder;
    private Collection<CacheEventListener> listeners;
    private CacheManager cacheManager;
    private boolean initialized = false;
    private ApplicationContext applicationContext;

    protected long slowCacheTime;
    protected long slowInvokeTime;

    private Map<MethodClassKey, Boolean> NO_KEY_CACHE = new ConcurrentHashMap<MethodClassKey, Boolean>();

    public void setSlowCacheTime(long slowCacheTime) {
        this.slowCacheTime = slowCacheTime;
    }

    public void setSlowInvokeTime(long slowInvokeTime) {
        this.slowInvokeTime = slowInvokeTime;
    }

    public void setCacheOperationParser(CacheOperationParser cacheOperationParser) {
        this.cacheOperationParser = cacheOperationParser;
    }

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
        //afterSingletonsInstantiated();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
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
        if ( this.cacheOperationParser == null ) {
            this.cacheOperationParser = new CacheOperationParser();
        }
        this.initialized = true;
        logger.info("eiscache start");
    }

    protected Class<?> getTargetClass(Object target) {
        //有可能是代理类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

    protected Object execute(Invoker invoker, Object target, Method method, Object[] args, Class<?> returnType) {

        try {
            if ( !this.initialized ) {
                return invoker.invoke();
            }
            Class<?> targetClass =  getTargetClass(target);
            if ( Boolean.TRUE.equals(NO_KEY_CACHE.get(new MethodClassKey(method, targetClass))) ) {
                return invoker.invoke();
            }
            long startTime = System.currentTimeMillis();
            Collection<CacheOperation> operations = this.cacheOperationParser.parser(method, targetClass);
            if ( !operations.isEmpty() ) {
                CacheInvocationContexts contexts = new CacheInvocationContexts(operations, target, method, args, returnType);

                //delete cache before invoke method
                processCacheRemove(contexts.getInvocationContext(CacheRemoveOperation.class), true, CacheExpressionEvaluator.UN_AVAILABLE);

                //get from cache or put
                Object result;
                //process cacheable
                if ( contexts.getInvocationContext(CacheableOperation.class) != null ) {
                    result = getFromCache(contexts.getInvocationContext(CacheableOperation.class));
                    if ( result == null ) {
                        result = invoker.invoke();
                        processCachePut(contexts.getInvocationContext(CacheableOperation.class), result);
                    }
                }
                //is remove so invoke method
                else {
                    result = invoker.invoke();
                }

                //delete cache after invoke method
                processCacheRemove(contexts.getInvocationContext(CacheRemoveOperation.class), false, result);
                long time = System.currentTimeMillis() - startTime;
                if ( logger.isDebugEnabled() ) {
                    logger.debug("process method " + targetClass.getName() + "#" + method.getName() + " cache operation time is {" + time + "}ms");
                }
                //缓存处理慢的方法调用全部日志记录下来
                if ( time >= slowCacheTime ) {
                    logger.warn("slow cache operation " + targetClass.getName() + "#" + method.getName() + " cache operation time is {" +time + "}ms");
                }
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
        if ( cacheRemoveOperation.isAllEntries() ) {
            doRemoveAll(invocationContext.getCache());
            logger.debug("process method " + invocationContext.getTargetClass().getName() + " remove all");
            return ;
        }
        result = beforeInvocation ? CacheExpressionEvaluator.UN_AVAILABLE : result;
        Cache cache = invocationContext.getCache();
        Object key = invocationContext.generateKey(result);

        Set<String> entityId = invocationContext.entityId(result);

        if ( key instanceof Collection) {
            doRemove(invocationContext.getCache(), ((Collection) key).toArray(), entityId);
        } else if ( key instanceof Object[] ) {
            doRemove(invocationContext.getCache(), (Object[]) key, entityId);
        } else {
            doRemove(cache, key, entityId);
        }
    }

    protected Object getFromCache(CacheInvocationContext invocationContext) {
        if ( invocationContext != null ) {
            Cache cache = invocationContext.getCache();
            return doGet(cache, invocationContext.generateKey(CacheExpressionEvaluator.UN_AVAILABLE), invocationContext.getReturnType());
        }
        return null;
    }

    protected void processCachePut(CacheInvocationContext invocationContext, Object result) {
        if ( invocationContext != null ) {
            Object key = invocationContext.generateKey(result);
            if ( !invocationContext.isCondition(result) ) {
                return ;
            }
            if ( key == null || "".equals(key) ) {
                NO_KEY_CACHE.put(new MethodClassKey(invocationContext.getMethod(), invocationContext.getTargetClass()), Boolean.TRUE);
                logger.warn( invocationContext.getTargetClass().getName() + "#" + invocationContext.getMethod().getName() + " cacheable no key ");
                return ;
            }
            CacheableOperation operation = (CacheableOperation) invocationContext.getCacheOperation();
            Set<String> entityId = invocationContext.entityId(result);

            if ( key.getClass().isArray() ) {
                for (Object o : (Object[]) key) {
                    doPut(entityId, invocationContext.getCache(), o, result, operation.getExpire(), operation.getTimeUnit());
                }
            } else if ( key instanceof Collection) {
                for (Object o : (Collection) key) {
                    doPut(entityId, invocationContext.getCache(), o, result, operation.getExpire(), operation.getTimeUnit());
                }
            } else {
                doPut(entityId, invocationContext.getCache(), key, result, operation.getExpire(), operation.getTimeUnit());
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

    private Cache getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    class CacheInvocationContexts {
        private Map<Class<? extends CacheOperation>, CacheInvocationContext> invocationContextMap ;

        CacheInvocationContexts(Collection<CacheOperation> cacheOperations, Object target, Method method, Object[] args, Class<?> returnType) {
            invocationContextMap = new HashMap<Class<? extends CacheOperation>, CacheInvocationContext>();
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

        CacheInvocationContext getInvocationContext(Class<? extends CacheOperation> operationClass) {
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
        private Class<?> targetClass;
        public CacheInvocationContext(Object target, Method method, Object[] args, Class<?> returnType, CacheOperation cacheOperation) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.returnType = returnType;
            this.cacheOperation = cacheOperation;
            this.targetClass = CacheAopExecutor.this.getTargetClass(target);
            this.cache = CacheAopExecutor.this.getCache(cacheName());
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
        public Class<?> getTargetClass() {
            return targetClass;
        }

        @Override
        public CacheOperation getCacheOperation() {
            return this.cacheOperation;
        }

        @Override
        public boolean isCondition(Object result) {
            Boolean condition = null;
            try {
                if ( "".equals(getCacheOperation().getCondition()) ) {
                    return true;
                }
                EvaluationContext context = buildContext(result);
                condition = evaluator.getValue(getCacheOperation().getCondition(), context, Boolean.class);
                if ( logger.isDebugEnabled() ) {
                    logger.debug(targetClass.getName() + "#" + method.getName() + " cache condition {" + getCacheOperation().getCondition() + "} is " + (condition == null ? "false" : condition));
                }
                return condition == null ? false : condition;
            } catch (Exception e) {
                logger.error(" evaluator condition is error , don't cache", e);
                return false;
            }
        }

        @Override
        public Object generateKey(Object result) {
            if ( "".equals(getCacheOperation().getKey()) ) {
                return "";
            }
            EvaluationContext context = buildContext(result);
            Object key = evaluator.getValue(getCacheOperation().getKey(), context);
            if ( logger.isDebugEnabled() ) {
                logger.debug(targetClass.getName() + "#" + method.getName() + " cache key {" + getCacheOperation().getKey() + "} is " + (key == null ? "null" : key));
            }
            return key;
        }

        @Override
        public Set<String> entityId(Object result) {
            if ( "".equals(getCacheOperation().getEntityId()) ) {
                return Collections.EMPTY_SET;
            }
            if ( getCacheOperation().getEntityId().contains("#result")
                    && result == null ) {
                return Collections.emptySet();
            }
            EvaluationContext context = buildContext(result);
            Set<Object> entityIds = evaluator.getValue(getCacheOperation().getEntityId(), context, Set.class);
            if ( logger.isDebugEnabled() ) {
                logger.debug(targetClass.getName() + "#" + method.getName() + " entity ids is " + Arrays.toString(entityIds.toArray(new Object[entityIds.size()])));
            }
            Set<String> idSet = new HashSet<String>();
            if ( entityIds != null && !entityIds.isEmpty())
            for (Object id : entityIds) {
                if ( id != null ) {
                    idSet.add(id.toString());
                }
            }
            return idSet;
        }

        @Override
        public String cacheName() {
            EvaluationContext context = buildContext(null);
            return evaluator.getValue(getCacheOperation().getCacheName(), context, String.class);
        }

        protected EvaluationContext buildContext(Object result) {
            CacheEvaluationContext context = new CacheEvaluationContext(this, getMethod(), getArgs(), evaluator.getParameterNameDiscoverer());
            if ( result == CacheExpressionEvaluator.UN_AVAILABLE ) {
                context.setUnavailable("result");
            }
            if ( result != CacheExpressionEvaluator.NO_RESULT ) {
                context.setVariable("result", result);
            }
            try {
                context.registerFunction("getFirstGenericType", BeanUtils.class.getMethod("getFirstGenericType", Class.class));
                context.registerFunction("getArrayHash", BeanUtils.class.getMethod("getArrayHash", Object.class));
            } catch (Exception e){
                logger.error("注册net.zdsoft.cache.utils.BeanUtils.getFirstGenericType失败", e);
                throw new Invoker.ThrowableWrapper(e);
            }
            context.setBeanResolver(new BeanFactoryResolver(beanFactory));
            return context;
        }
    }
}
