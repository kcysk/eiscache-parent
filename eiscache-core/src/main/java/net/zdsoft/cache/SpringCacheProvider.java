package net.zdsoft.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author shenke
 * @since 17-9-4下午10:43
 */
@Component
public class SpringCacheProvider implements CachingProvider, BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;

    @Override
    public CacheManager getCacheManager() {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
