/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.zdsoft.cache.integration.spring;

import net.zdsoft.cache.Constant;
import net.zdsoft.cache.annotation.EnableCache;
import org.springframework.context.annotation.AutoProxyRegistrar;

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
        result.add(AutoProxyRegistrar.class.getName());
        //通过DynamicClassFilter控制
        result.add(Constant.PROXY_CONFIGURATION_CLASS);
        return result.toArray(new String[result.size()]);
    }

}
