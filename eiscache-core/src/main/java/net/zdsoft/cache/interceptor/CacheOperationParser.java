package net.zdsoft.cache.interceptor;

import net.zdsoft.cache.BeanUtils;
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

    /**
     *
     * @param method
     * @param targetClass 原始类型
     * @return
     */
    public Collection<CacheOperation> parser(Method method, Class<?> targetClass) {

        MethodClassKey key = new MethodClassKey(method, targetClass);
        Collection<CacheOperation> cacheOperations = cached.get(key);
        if ( cacheOperations != null && cacheOperations.size() > 0) {
            return cacheOperations;
        }
        cacheOperations = new ArrayList<>();

        CacheDefault cacheDefault = getCacheDefault(targetClass);

        Cacheable cacheable = getAnnotation(targetClass, method, Cacheable.class);
        CacheRemove cacheRemove = getAnnotation(targetClass, method, CacheRemove.class);
        if ( cacheable != null ){
            cacheOperations.add(parseCacheable(cacheable, cacheDefault));
        }
        if ( cacheRemove != null ) {
            cacheOperations.add(parseCacheRemove(cacheRemove, cacheDefault));
        }
        cached.put(key, cacheOperations);
        return cacheOperations;
    }

    public <T extends Annotation> T getAnnotation(Class<?> targetClass, Method method, Class<T> annotationType) {
        T t = targetClass.getAnnotation(annotationType);
        AnnotationFilter<T> filter = new AnnotationFilter<T>() {
            @Override
            public boolean filter(T t) {
                return t != null;
            }
        };
        //接口优先原则，顶层接口滞后原则
        t = t == null ? filterAnnotation(method, getInterfaces(targetClass), annotationType, filter) : t;
        t = t == null ? filterAnnotation(method, getSupperClasses(targetClass), annotationType, filter) : t;
        return t;
    }

    public <T extends Annotation> T filterAnnotation(Method method, Collection<Class<?>> targetClass, Class<T> type ,AnnotationFilter<T> filter) {
        for (Class<?> aClass : targetClass) {
            Method sameMethod = BeanUtils.getSameMethod(aClass, method);
            if (sameMethod != null && filter.filter(sameMethod.getAnnotation(type)) ) {
                return sameMethod.getAnnotation(type);
            }
        }
        return null;
    }

    public CacheDefault getCacheDefault(Class<?> targetClass) {
        CacheDefault cacheDefault = targetClass.getAnnotation(CacheDefault.class);
        if ( cacheDefault != null && !"".equals(cacheDefault.cacheName()) ) {
            return cacheDefault;
        }

        for (Class<?> aClass : getInterfaces(targetClass)) {
            cacheDefault = aClass.getAnnotation(CacheDefault.class);
            if ( cacheDefault != null && !"".equals(cacheDefault.cacheName()) ) {
                return cacheDefault;
            }
        }

        for (Class<?> aClass : getSupperClasses(targetClass)) {
            cacheDefault = aClass.getAnnotation(CacheDefault.class);
            if ( cacheDefault != null && !"".equals(cacheDefault.cacheName()) ) {
                return cacheDefault;
            }
        }
        return cacheDefault;
    }


    private interface AnnotationFilter<T extends Annotation> {
        boolean filter(T t);
    }

    public List<Class<?>> getSupperClasses(Class<?> targetClass) {
        if ( targetClass.isInterface() ) {
            return new ArrayList<>();
        }
        Class<?> parentClass = targetClass.getSuperclass();
        if ( Object.class.equals(parentClass) ) {
            return Collections.emptyList();
        }
        List<Class<?>> parentClassList = new ArrayList<>();
        parentClassList.add(parentClass);
        parentClassList.addAll(getSupperClasses(parentClass));
        return parentClassList;
    }

    public List<Class<?>> getInterfaces(Class<?> targetClass) {
        if ( targetClass.isInterface() ) {
            Class<?>[] interfaces = targetClass.getInterfaces();
            List<Class<?>> interfaceList = new ArrayList<>();
            if ( interfaces != null && interfaces.length != 0 ) {
                interfaceList.addAll(Arrays.asList(interfaces));
                for (Class<?> aClass : interfaces) {
                    interfaceList.addAll(getInterfaces(aClass));
                }
            }
            return interfaceList;
        } else {
            List<Class<?>> interfaceList = new ArrayList<>();
            Class<?>[] interfaces = targetClass.getInterfaces();
            if ( interfaces != null && interfaces.length != 0 ) {
                interfaceList.addAll(Arrays.asList(interfaces));
                for (Class<?> aClass : interfaces) {
                    interfaceList.addAll(getInterfaces(aClass));
                }
            }
            return interfaceList;
        }
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
                .setCondition(cacheable.condition())
                .setEntityId(cacheable.entityId());
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
                .setKey(cacheRemove.key())
                .setEntityId(cacheRemove.entityId());
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
