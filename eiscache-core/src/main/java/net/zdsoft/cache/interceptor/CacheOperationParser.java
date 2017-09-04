package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheDefault;
import net.zdsoft.cache.annotation.CacheOperation;
import net.zdsoft.cache.annotation.Cacheable;
import org.springframework.core.MethodClassKey;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenke
 * @since 2017.09.04
 */
final public class CacheOperationParser {

    private Map<MethodClassKey, Collection<CacheOperation>> cached = new HashMap<>();

    private static final Collection<CacheOperation> EMPTY = Collections.emptyList();

    public Collection<CacheOperation> parser(Method method) {
        //TODO proxy
        Class<?> targetClass =  method.getDeclaringClass();
        MethodClassKey key = new MethodClassKey(method, targetClass);
        Collection<CacheOperation> cacheOperations = cached.get(key);
        if ( cacheOperations == EMPTY) {
            return EMPTY;
        }

        //解析
        CacheDefault cacheDefault = targetClass.getAnnotation(CacheDefault.class);

        Cacheable cacheable = method.getAnnotation(Cacheable.class);


        return null;
    }

    private CacheOperation parseCacheable(Cacheable cacheable, CacheDefault cacheDefault) {
        CacheableOperation.Builder builder = new CacheableOperation.Builder();
        builder.setExpire(cacheable.expire())
                .setTimeToLive(cacheable.timeToLive())
                .setUnless(cacheable.unless())
                .setTimeUnit(cacheable.timeUnit())
                .setCacheName(cacheable.cacheName())
                .setKey(cacheable.key())
                .setCondition(cacheable.condition());

        CacheableOperation cacheableOperation = new CacheableOperation(builder);
        if ( cacheableOperation.getCacheName().equals("") ) {

        }
        return cacheableOperation;
    }

}
