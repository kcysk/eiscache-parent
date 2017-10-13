package net.zdsoft.cache.aop.interceptor;

import net.zdsoft.cache.utils.BeanUtils;
import net.zdsoft.cache.support.MethodClassKey;
import net.zdsoft.cache.annotation.CacheDefault;
import net.zdsoft.cache.core.CacheOperation;
import net.zdsoft.cache.annotation.CacheRemove;
import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.core.support.CacheRemoveOperation;
import net.zdsoft.cache.core.support.CacheableOperation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author shenke
 * @since 2017.09.04
 */
final public class CacheOperationParser {

    private Map<MethodClassKey, Collection<CacheOperation>> cached = new HashMap<MethodClassKey, Collection<CacheOperation>>();

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
        if ( cacheOperations != null ) {
            return cacheOperations;
        }
        cacheOperations = new ArrayList<CacheOperation>();

        CacheDefault cacheDefault = getCacheDefault(targetClass);

        Cacheable cacheable = getAnnotation(targetClass, method, Cacheable.class);
        CacheRemove cacheRemove = getAnnotation(targetClass, method, CacheRemove.class);
        if ( cacheable != null ){
            cacheOperations.add(parseCacheable(cacheable, cacheDefault));
        }
        if ( cacheRemove != null ) {
            cacheOperations.add(parseCacheRemove(cacheRemove, cacheDefault));
        }
        if ( cacheOperations.isEmpty() ) {
            cached.put(key, EMPTY);
        } else {
            cached.put(key, cacheOperations);
        }
        return cacheOperations;
    }

    public <T extends Annotation> T getAnnotation(Class<?> targetClass, Method method, Class<T> annotationType) {
        T t = targetClass.getAnnotation(annotationType);
        //接口优先原则，顶层接口滞后原则
        t = t == null ? filterAnnotation(method, getInterfaces(targetClass), annotationType) : t;
        t = t == null ? filterAnnotation(method, getSupperClasses(targetClass), annotationType) : t;
        return t;
    }

    public <T extends Annotation> T filterAnnotation(Method method, Collection<Class<?>> targetClass, Class<T> type) {
        for (Class<?> aClass : targetClass) {
            Method sameMethod = BeanUtils.getSameMethod(aClass, method);
            if (sameMethod != null && sameMethod.getAnnotation(type) != null ) {
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

    public List<Class<?>> getSupperClasses(Class<?> targetClass) {
        if ( targetClass.isInterface() ) {
            return new ArrayList<Class<?>>();
        }
        Class<?> parentClass = targetClass.getSuperclass();
        if ( Object.class.equals(parentClass) ) {
            return Collections.emptyList();
        }
        List<Class<?>> parentClassList = new ArrayList<Class<?>>();
        parentClassList.add(parentClass);
        parentClassList.addAll(getSupperClasses(parentClass));
        return parentClassList;
    }

    public List<Class<?>> getInterfaces(Class<?> targetClass) {
        if ( targetClass.isInterface() ) {
            Class<?>[] interfaces = targetClass.getInterfaces();
            List<Class<?>> interfaceList = new ArrayList<Class<?>>();
            if ( interfaces != null && interfaces.length != 0 ) {
                interfaceList.addAll(Arrays.asList(interfaces));
                for (Class<?> aClass : interfaces) {
                    interfaceList.addAll(getInterfaces(aClass));
                }
            }
            return interfaceList;
        } else {
            List<Class<?>> interfaceList = new ArrayList<Class<?>>();
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
        return new CacheableOperation(builder);
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
        return new CacheRemoveOperation(builder);
    }
}
