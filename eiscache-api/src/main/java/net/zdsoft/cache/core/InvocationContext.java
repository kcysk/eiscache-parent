package net.zdsoft.cache.core;

import net.zdsoft.cache.annotation.Cacheable;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author shenke
 * @since 17-9-4上午12:10
 */
public interface InvocationContext<O extends CacheOperation>  {

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Class<?> getReturnType();

    Class<?> getTargetClass();

    O getCacheOperation();

    Cache getCache();

    /**
     * 通过计算springEL表达式
     * @see Cacheable#condition()
     * @see
     * @param result  方法实际执行结果
     */
    boolean isCondition(Object result);

    /**
     * evaluator springEL for result
     * @see Cacheable#key()
     */
    Object generateKey(Object result);

    Set entityId(Object result);

    String cacheName();
}
