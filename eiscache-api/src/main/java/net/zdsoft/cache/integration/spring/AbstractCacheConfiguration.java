package net.zdsoft.cache.integration.spring;

import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author shenke
 * @since 17-8-29下午10:17
 */
public abstract class AbstractCacheConfiguration implements ImportAware {

    protected AnnotationAttributes enableCache;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        enableCache = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCache.class.getName(), false));
    }

}
