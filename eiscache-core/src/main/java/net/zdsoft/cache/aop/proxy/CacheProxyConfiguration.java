package net.zdsoft.cache.aop.proxy;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.DynamicCacheClassFilter;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.context.annotation.Bean;

/**
 * @author shenke
 * @since 2017.09.06
 */
//@Configuration
public class CacheProxyConfiguration extends AbstractCacheConfiguration {

    @Bean
    public CacheInterceptor cacheInterceptor(TypeDescriptor typeDescriptor, CacheOperationParser parser){
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        cacheInterceptor.setTypeDescriptor(typeDescriptor);
        cacheInterceptor.setCacheOperationParser(parser);
        return cacheInterceptor;
    }

    @Bean(name = Constant.BEAN_NAME_ADVISOR)
    @Override
    public AbstractBeanFactoryPointcutAdvisor cacheAdvisor(CacheInterceptor cacheInterceptor, CacheOperationParser cacheOperationParser, DynamicCacheClassFilter filter) {
        return super.cacheAdvisor(cacheInterceptor, cacheOperationParser, filter);
    }
}
