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
package net.zdsoft.cache.monitor;

import java.util.List;

/**
 * @author shenke
 * @since 2017.10.13
 */
public interface CacheTargetMBean {

    List<String> getCacheClasses();

    List<String> getCacheMethods(String className);

    List<String> getBlackListOfClass();

    List<String> getBlackListOfMethod(String className);

    void addClassBlackList(String className);

    /**
     * @param methodName ex:<code>net.zdsoft.cache.monitor.CacheTargetMBean#getBlackListOfClass</code>
     */
    void addMethodBlackList(String methodName);

    void removeClassBlackList(String className);

    /**
     * @param methodName ex:<code>net.zdsoft.cache.monitor.CacheTargetMBean#getBlackListOfClass</code>
     */
    void removeMethodBlack(String methodName);
}
