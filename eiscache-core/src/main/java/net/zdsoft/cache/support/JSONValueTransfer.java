package net.zdsoft.cache.support;

import com.alibaba.fastjson.JSON;
import net.zdsoft.cache.transfer.ValueTransfer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.20
 */
public class JSONValueTransfer implements ValueTransfer {

    @Override
    public <T> String transfer(T t) {
        return JSON.toJSONString(t);
    }

    @Override
    public <K, V> Map<K, V> parseFor(String s, Type kClass, Type vClass) {
        if ( s == null ) {
            return null;
        }
        Map<Object, Object> map = JSON.parseObject(s, Map.class);
        Map<K,V> realTypeMap = new HashMap<K,V>(15);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            K k = parseForNative(toString(entry.getKey()), kClass);
            V v = parseForNative(toString(entry.getValue()), vClass);
            realTypeMap.put(k, v);
        }
        return realTypeMap;
    }

    @Override
    public <K, V> Map<K, V> parseFor(String s, Type genericType) {
        if ( genericType instanceof Class ) {
            throw new IllegalArgumentException("illegal Argument");
        }
        return parseForNative(s, genericType);
    }

    private String toString(Object t) {
        if ( t != null ) {
            return JSON.toJSONString(t);
        }
        return null;
    }

    @Override
    public <T> Set<T> parseForSet(String s, Type tClass) {
        List<T> ts = parseForList(s, tClass);
        if ( ts != null ) {
            return new HashSet<T>(ts);
        }
        return null;
    }

    @Override
    public <T> List<T> parseForList(String s, Type tType) {
        if ( s == null ) {
            return null;
        }
        if ( tType instanceof Class ) {
            return JSON.parseArray(s, (Class<T>) tType);
        }
        return parseForNative(s, tType);
    }

    @Override
    public <T> T parseForNative(String s, Type tClass) {
        if ( s == null ) {
            return null;
        }
        return JSON.parseObject(s, tClass);
    }
}
