package net.zdsoft.cache;

import net.zdsoft.cache.expression.CacheExpressionEvaluator;
import net.zdsoft.cache.interceptor.CacheOperationParser;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class CacheContext {

    public static final Object UN_AVAILABLE = new Object();
    public static final Object NO_RESULT = new Object();

    public static CacheExpressionEvaluator getExpressionEvaluator() {

        return null;
    }

    public static BeanFactory getBeanFactory(){
        return null;
    }

    public static CacheOperationParser getCacheOperationParser() {

        return null;
    }
}
