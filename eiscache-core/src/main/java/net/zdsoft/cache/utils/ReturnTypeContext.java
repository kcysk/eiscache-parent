package net.zdsoft.cache.utils;

/**
 * @author shenke
 * @since 2017.09.15
 */
public class ReturnTypeContext {

    private static ThreadLocal<Class<?>> entityTypeLocal = new ThreadLocal<>();

    private static ThreadLocal<Class<?>> returnTypeLocal = new ThreadLocal<>();


    public static Class<?> getEntityType() {
        return entityTypeLocal.get();
    }

    public static void registerEntityType(Class<?> entityType) {
        entityTypeLocal.set(entityType);
    }

    public static void removeEntityType() {
        entityTypeLocal.remove();
    }

    public static Class<?> getReturnType() {
        return returnTypeLocal.get();
    }

    public static void registerReturnType(Class<?> returnType) {
        returnTypeLocal.set(returnType);
    }

    public static void removeReturnType() {
        returnTypeLocal.remove();
    }
}
