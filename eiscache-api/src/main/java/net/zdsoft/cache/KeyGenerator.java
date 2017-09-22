package net.zdsoft.cache;

import net.zdsoft.cache.annotation.Cacheable;

import java.lang.reflect.Method;

/**
 *
 * key生成策略，当key不指定时使用系统的KeyGenerator生成Key
 * @see Cacheable#key()
 * @author shenke
 * @since 2017.09.22
 */
public interface KeyGenerator {

    Object generate(Object target, Method method, Object ... params);

}
