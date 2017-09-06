package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheDefault;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.annotation.CacheRemove;
import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.core.support.CacheableOperation;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;

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

        cacheOperations = new ArrayList<>();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if ( cacheable != null ){
            cacheOperations.add(parseCacheable(cacheable, cacheDefault));
        }
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        if ( cacheRemove != null ) {
            cacheOperations.add(parseCacheRemove(cacheRemove, cacheDefault));
        }
        cached.put(key, cacheOperations);
        return cacheOperations;
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
        if ( "".equals(builder.getCacheName()) && cacheDefault != null ) {
            builder.setCacheName(cacheDefault.cacheName());
        }
        CacheableOperation cacheableOperation = new CacheableOperation(builder);
        return cacheableOperation;
    }

    private CacheOperation parseCacheRemove(CacheRemove cacheRemove, CacheDefault cacheDefault) {
        CacheRemoveOperation.Builder builder = new CacheRemoveOperation.Builder();
        builder.setAfterInvocation(cacheRemove.afterInvocation())
                .setCacheName(cacheRemove.cacheName())
                .setCondition(cacheRemove.condition())
                .setKey(cacheRemove.key());
        if ( "".equals(builder.getCacheName()) && cacheDefault != null ) {
            builder.setCacheName(cacheDefault.cacheName());
        }
        CacheRemoveOperation cacheRemoveOperation = new CacheRemoveOperation(builder);
        return cacheRemoveOperation;
    }

    class MethodClassKey implements Comparable<MethodClassKey> {
        private final Method method;

        private final Class<?> targetClass;


        /**
         * Create a key object for the given method and target class.
         * @param method the method to wrap (must not be {@code null})
         * @param targetClass the target class that the method will be invoked
         * on (may be {@code null} if identical to the declaring class)
         */
        public MethodClassKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }


        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MethodClassKey)) {
                return false;
            }
            MethodClassKey otherKey = (MethodClassKey) other;
            return (this.method.equals(otherKey.method) &&
                    ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
        }

        @Override
        public int hashCode() {
            return this.method.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
        }

        @Override
        public String toString() {
            return this.method + (this.targetClass != null ? " on " + this.targetClass : "");
        }

        @Override
        public int compareTo(MethodClassKey other) {
            int result = this.method.getName().compareTo(other.method.getName());
            if (result == 0) {
                result = this.method.toString().compareTo(other.method.toString());
                if (result == 0 && this.targetClass != null) {
                    result = this.targetClass.getName().compareTo(other.targetClass.getName());
                }
            }
            return result;
        }

    }
}
