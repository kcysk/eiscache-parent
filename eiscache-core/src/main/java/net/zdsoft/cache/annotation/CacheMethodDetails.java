package net.zdsoft.cache.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 17-9-3下午11:18
 */
public interface CacheMethodDetails {

    Object getTarget();

    Method getMethod();

    Object[] getArgs();

    <A extends Annotation> A getAnnotation();

}
