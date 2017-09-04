package net.zdsoft.cache.annotation;

import net.zdsoft.cache.integration.spring.CacheConfigurationSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shenke
 * @since 2017.09.04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheConfigurationSelector.class)
public @interface EnableCache {

    AdviceMode mode() default AdviceMode.ASPECTJ;

    int order() default Ordered.LOWEST_PRECEDENCE;
}
