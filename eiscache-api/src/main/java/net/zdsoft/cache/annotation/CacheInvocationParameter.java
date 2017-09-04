package net.zdsoft.cache.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface CacheInvocationParameter {

    Class<?> getRowType();

    Object getValue();

    Set<Annotation> getAnnotations();
}
