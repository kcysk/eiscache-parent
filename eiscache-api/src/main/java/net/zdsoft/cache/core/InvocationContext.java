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
package net.zdsoft.cache.core;

import net.zdsoft.cache.annotation.Cacheable;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author shenke
 * @since 17-9-4上午12:10
 */
public interface InvocationContext<O extends CacheOperation>  {

    Method getMethod();

    Object getTarget();

    Object[] getArgs();

    Class<?> getReturnType();

    Class<?> getTargetClass();

    O getCacheOperation();

    Cache getCache();

    /**
     * 通过计算springEL表达式
     * @see Cacheable#condition()
     * @see
     * @param result  方法实际执行结果
     */
    boolean isCondition(Object result);

    /**
     * evaluator springEL for result
     * @see Cacheable#key()
     */
    Object generateKey(Object result);

    Set entityId(Object result);

    String cacheName();
}
