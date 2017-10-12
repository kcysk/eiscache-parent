package net.zdsoft.cache.annotation;

import net.zdsoft.cache.integration.spring.Advice;
import net.zdsoft.cache.integration.spring.CacheConfigurationSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spring 版本低于3.1 请不要使用 参见
 * {@code net.zdsoft.cache.aop.aspectj.CacheAspectj} <br>
 * @author shenke
 * @since 2017.09.04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheConfigurationSelector.class)
public @interface EnableCache {

    Advice advice() default Advice.NONE;

    int order() default Ordered.LOWEST_PRECEDENCE;
}
