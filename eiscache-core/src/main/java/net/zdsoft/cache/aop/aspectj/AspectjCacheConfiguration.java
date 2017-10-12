package net.zdsoft.cache.aop.aspectj;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.CacheBeanFactoryPointCutAdvisor;
import net.zdsoft.cache.aop.DynamicCacheClassFilter;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.springframework.context.annotation.Bean;

/**
 * @author shenke
 * @since 2017.08.30
 */
//@Configuration
public class AspectjCacheConfiguration extends AbstractCacheConfiguration {

    @Bean(name = Constant.BEAN_NAME_ASPECTJ)
    public CacheAspectj cacheAspect(TypeDescriptor typeDescriptor,
                                    CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        CacheAspectj aspectj = new CacheAspectj();
        CacheBeanFactoryPointCutAdvisor advisor = (CacheBeanFactoryPointCutAdvisor) cacheAdvisor(null, cacheOperationParser, filter);
        aspectj.setAdvisor(advisor);
        aspectj.setTypeDescriptor(typeDescriptor);
        return aspectj;
    }
}
