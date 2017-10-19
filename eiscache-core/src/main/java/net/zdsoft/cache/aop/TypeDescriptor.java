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
package net.zdsoft.cache.aop;

import net.zdsoft.cache.utils.TypeBuilder;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author shenke
 * @since 2017.09.21
 */
public interface TypeDescriptor {

    /**
     * 解决因为泛型导致无法获取实际类型的问题 <br>
     * 该方法实现取决于基类方法的返回类型 <br>
     * 若基类方法的返回类型是Map<String,List<T>> 则该方法就应该构建这个Map的实际类型信息
     * @param invocation {@link MethodInvocation}
     * @param targetClass 实际的对象类型信息，不要从invocation获取，可能是代理对象
     */
    TypeBuilder buildType(MethodInvocation invocation, Class<?> targetClass);

}
