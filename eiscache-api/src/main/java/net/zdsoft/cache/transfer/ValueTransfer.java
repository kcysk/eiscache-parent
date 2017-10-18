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
package net.zdsoft.cache.transfer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.04
 */
public interface ValueTransfer {

    <T> String transfer(T t);

    <K,V> Map<K, V> parseFor(String s, Type kGenericType, Type vGenericType);

    <K,V> Map<K, V> parseFor(String s, Type genericType);

    <T> Set<T> parseForSet(String s, Type tGenericType);

    <T> List<T> parseForList(String s, Type tGenericType);

    /**
     * <p>
     *    <li>
     *        如果目标数据类型是Map，List，Set等复杂数据类型，<br>
     *        并且type是{@code java.lang.reflect.ParameterizedType},则使应该方法支持
     *    </li>
     *    <li>
     *        如果type是java原生类型的包装类型，ex：Long.class，也应该支持
     *    </li>
     * </p>
     * @param s
     * @param tGenericType
     * @param <T>
     * @return
     */
    <T> T parseForNative(String s, Type tGenericType);
}
