package net.zdsoft.cache.aop.proxy;

import net.zdsoft.cache.aop.AbstractCacheConfiguration;
import net.zdsoft.cache.aop.TypeDescriptor;
import net.zdsoft.cache.aop.interceptor.CacheOperationParser;
import org.springframework.context.annotation.Bean;

/**
 * @author shenke
 * @since 2017.09.06
 */
public class CacheProxyConfiguration extends AbstractCacheConfiguration {

    @Bean
    public CacheInterceptor cacheInterceptor(TypeDescriptor typeDescriptor, CacheOperationParser parser){
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        cacheInterceptor.setTypeDescriptor(typeDescriptor);
        cacheInterceptor.setCacheOperationParser(parser);
        setSlowTime(cacheInterceptor);
        return cacheInterceptor;
    }

}
