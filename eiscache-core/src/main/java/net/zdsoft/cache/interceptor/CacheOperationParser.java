package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.annotation.CacheDefault;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.annotation.CacheRemove;
import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.core.support.CacheableOperation;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author shenke
 * @since 2017.09.04
 */
final public class CacheOperationParser {

    private Map<MethodClassKey, Collection<CacheOperation>> cached = new HashMap<>();

    private static final Collection<CacheOperation> EMPTY = Collections.emptyList();

    public Collection<CacheOperation> parser(Method method) {
        return parser(method, null);
    }

    public Collection<CacheOperation> parser(Method method, Class<?> targetClass) {

        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        if ( targetClass != null ) {
            //get real method
            Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
            method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        } else {
            targetClass = method.getDeclaringClass();
        }

        MethodClassKey key = new MethodClassKey(method, targetClass);
        Collection<CacheOperation> cacheOperations = cached.get(key);
        if ( cacheOperations != null && cacheOperations.size() > 0) {
            return cacheOperations;
        }


        List<CacheDefault> cacheDefaults = new ArrayList<>(10);
        List<Cacheable> cacheables = new ArrayList<>(10);
        List<CacheRemove> cacheRemoves = new ArrayList<>(10);
        //解析
        if ( !targetClass.isInterface() ) {
            Type[] interfaces = targetClass.getGenericInterfaces();
            for (Type type : interfaces) {
                if ( type instanceof ParameterizedType) {
                    Class<?> clazz = (Class<?>) ((ParameterizedType)type).getRawType();
                    parseAnnotation(clazz, cacheDefaults, cacheables, cacheRemoves, method);
                }else {
                    parseAnnotation((Class<?>)type, cacheDefaults, cacheables, cacheRemoves, method);
                }
            }

        }
        CacheDefault cacheDefault = targetClass.getAnnotation(CacheDefault.class);
        cacheDefault = cacheDefault == null && !cacheDefaults.isEmpty() ? cacheDefaults.get(0) : cacheDefault;

        cacheOperations = new ArrayList<>();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        cacheable = cacheable == null && !cacheables.isEmpty() ? cacheables.get(0) : cacheable;
        if ( cacheable != null ){
            cacheOperations.add(parseCacheable(cacheable, cacheDefault));
        }
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        cacheRemove = cacheRemove == null && !cacheRemoves.isEmpty() ? cacheRemoves.get(0) : cacheRemove;
        if ( cacheRemove != null ) {
            cacheOperations.add(parseCacheRemove(cacheRemove, cacheDefault));
        }
        cached.put(key, cacheOperations);
        return cacheOperations;
    }

    private void parseAnnotation(Class<?> objClass, Collection<CacheDefault> cacheDefaults,
                                 Collection<Cacheable> cacheables,
                                 Collection<CacheRemove> cacheRemoves, Method originMethod) {
        CacheDefault cacheDefault = objClass.getAnnotation(CacheDefault.class);
        if ( cacheDefault != null ) {
            cacheDefaults.add(cacheDefault);
        }
        for (Method im : objClass.getDeclaredMethods()) {
            if ( !im.getName().equals(originMethod.getName()) ) {
                continue;
            }
            if ( im.getAnnotation(Cacheable.class) != null ) {
                cacheables.add(im.getAnnotation(Cacheable.class));
            }
            if ( im.getAnnotation(CacheRemove.class) != null ) {
                cacheRemoves.add(im.getAnnotation(CacheRemove.class));
            }
        }
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
