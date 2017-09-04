package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.Cache;
import net.zdsoft.cache.CacheContext;
import net.zdsoft.cache.core.CacheInvocationContext;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.expression.CacheEvaluationContext;
import net.zdsoft.cache.expression.CacheExpressionEvaluator;
import org.springframework.context.expression.BeanFactoryResolver;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class DefaultCacheInvocationContext implements CacheInvocationContext {

    private Object target;
    private Method method;
    private String cacheName;
    private Object[] args;
    private Class<?> returnType;
    private Cache cache;

    public DefaultCacheInvocationContext(Object target, Method method, Object[] args, Class<?> returnType, Cache cache) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.cache = cache;
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
    public Object evaluate(String springELExpression, Object result) {
        CacheExpressionEvaluator evaluator = CacheContext.getExpressionEvaluator();
        CacheEvaluationContext context = new CacheEvaluationContext(this, getMethod(), getArgs(), evaluator.getParameterNameDiscoverer());
        if ( result == CacheContext.UN_AVAILABLE ) {
            context.setUnavailable("result");
        }
        if ( result != CacheContext.NO_RESULT ) {
            context.setVariable("result", result);
        }
        context.setBeanResolver(new BeanFactoryResolver(CacheContext.getBeanFactory()));
        return evaluator.getValue(springELExpression, context);
    }

    @Override
    public CacheOperation getCacheOperation(Class<? extends CacheOperation> cType) {
        CacheOperationParser cacheOperationParser = CacheContext.getCacheOperationParser();
        for (CacheOperation cacheOperation : cacheOperationParser.parser(getMethod())) {
            if ( cacheOperation.getClass().equals(cType) ) {
                return cacheOperation;
            }
        }
        return null;
    }
}
