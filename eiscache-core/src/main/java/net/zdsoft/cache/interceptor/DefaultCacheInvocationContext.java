package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.CacheContext;
import net.zdsoft.cache.annotation.CacheInvocationContext;
import net.zdsoft.cache.annotation.CacheOperation;
import net.zdsoft.cache.annotation.Cacheable;
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

    public DefaultCacheInvocationContext(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
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
    public Object evaluate(String springELExpression, Object result) {
        CacheExpressionEvaluator evaluator = CacheContext.getExpressionEvaluator();
        CacheEvaluationContext context = new CacheEvaluationContext(this, method, args, evaluator.getParameterNameDiscoverer());
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
    public <C extends CacheOperation> C getCacheOperation(Class<C> cType) {

        return null;
    }
}
