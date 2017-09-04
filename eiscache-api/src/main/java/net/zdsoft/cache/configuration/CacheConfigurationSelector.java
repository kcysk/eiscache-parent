package net.zdsoft.cache.configuration;

import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author shenke
 * @since 2017.08.30
 */
public class CacheConfigurationSelector extends AdviceModeImportSelector<EnableCache> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        if ( AdviceMode.PROXY.equals(adviceMode) ){
            return getProxyImport();
        }

        return getAspectJImport();
    }

    private String[] getAspectJImport() {
        return new String[]{"net.zdsoft.cache.aspectj.AspectjCacheConfiguration"};
    }

    private String[] getProxyImport() {
        //result.add(AutoProxyRegistrar.class.getName());
        //result.add(ProxyCachingConfiguration.class.getName());
        return new String[]{AutoProxyRegistrar.class.getName(), ""};
    }

}
