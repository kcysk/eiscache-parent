package net.zdsoft.cache.configuration;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface ValueTransfer {

    <T> String transfer(T t);

    <K,V> Map<K, V> parseFor(String s, Type kGenericType, Type vGenericType);

    <K,V> Map<K, V> parseFor(String s, Type genericType);

    <T> Set<T> parseForSet(String s, Type tGenericType);

    <T> List<T> parseForList(String s, Type tGenericType);

    /**
     * <p>
     *    <li>
     *        如果目标数据类型是Map，List，Set等复杂数据类型，<br>
     *        并且type是{@code java.lang.reflect.ParameterizedType},则使应该方法支持
     *    </li>
     *    <li>
     *        如果type是java原生类型的包装类型，ex：Long.class，也应该支持
     *    </li>
     * </p>
     * @param s
     * @param tGenericType
     * @param <T>
     * @return
     */
    <T> T parseForNative(String s, Type tGenericType);
}
