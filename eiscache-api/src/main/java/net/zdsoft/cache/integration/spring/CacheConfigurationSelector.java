package net.zdsoft.cache.integration.spring;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.annotation.EnableCache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenke
 * @since 2017.08.30
 */
public class CacheConfigurationSelector extends AdviceImportSelector<EnableCache> {

    @Override
    protected String[] selectImports(Advice advice) {
        if ( Advice.PROXY.equals(advice) ){
            return getProxyImport();
        }
        return getAspectJImport();
    }

    private String[] getAspectJImport() {
        return new String[]{Constant.ASPECTJ_CONFIGURATION_CLASS};
    }

    private String[] getProxyImport() {
        List<String> result = new ArrayList<String>(2);
        //result.add(AutoProxyRegistrar.class.getName());
        //通过DynamicClassFilter控制
        result.add(Constant.PROXY_CONFIGURATION_CLASS);
        return result.toArray(new String[result.size()]);
    }

}
