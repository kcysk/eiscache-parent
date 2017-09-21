package net.zdsoft.cache.integration.spring;

import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

import java.util.ArrayList;
import java.util.List;

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
        List<String> result = new ArrayList<String>(2);
        result.add(AutoProxyRegistrar.class.getName());
        result.add("net.zdsoft.cache.proxy.CacheProxyConfiguration");
        return result.toArray(new String[result.size()]);
    }

}
