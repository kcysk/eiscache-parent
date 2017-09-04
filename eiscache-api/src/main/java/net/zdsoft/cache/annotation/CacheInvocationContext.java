package net.zdsoft.cache.annotation;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 17-9-4上午12:10
 */
public interface CacheInvocationContext  {

    Method getMethod();

    Object getTarget();

    /**
     * 解析springEL表达式
     * @param springELExpression 表达式
     */
    Object evaluate(String springELExpression, Object result);

    <C extends CacheOperation> C getCacheOperation(Class<C> cType);
}
