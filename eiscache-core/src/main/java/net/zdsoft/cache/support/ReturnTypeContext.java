package net.zdsoft.cache.support;

import java.lang.reflect.Type;

/**
 * @author shenke
 * @since 2017.09.15
 */
public class ReturnTypeContext {

    private static ThreadLocal<Class<?>> entityTypeLocal = new ThreadLocal<Class<?>>();

    private static ThreadLocal<Type> returnTypeLocal = new ThreadLocal<Type>();

    public static Class<?> getEntityType() {
        return entityTypeLocal.get();
    }

    public static void registerEntityType(Class<?> entityType) {
        entityTypeLocal.set(entityType);
    }

    public static void removeEntityType() {
        entityTypeLocal.remove();
    }

    public static Type getReturnType() {
        return returnTypeLocal.get();
    }

    public static void registerReturnType(Type returnType) {
        returnTypeLocal.set(returnType);
    }

    public static void removeReturnType() {
        returnTypeLocal.remove();
    }
}
