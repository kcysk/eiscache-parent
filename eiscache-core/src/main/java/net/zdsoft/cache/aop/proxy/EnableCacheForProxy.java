package net.zdsoft.cache.aop.proxy;

import net.zdsoft.cache.aop.aspectj.AspectjCacheConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shenke
 * @since 2017.10.12
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AspectjCacheConfiguration.class)
public @interface EnableCacheForProxy {

}
