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
package net.zdsoft.cache.configuration;

import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.transfer.ByteTransfer;
import net.zdsoft.cache.transfer.ValueTransfer;

/**
 * @author shenke
 * @since 17-9-3下午11:27
 */
public interface Configuration {

    /**
     * 过期时间配置
     */
    ExpiryPolicy getExpiry();

    /**
     * 数据类型转换接口
     */
    ValueTransfer getValueTransfer();

    /**
     * 字节转换接口
     */
    ByteTransfer getByteTransfer();

    Class<?> getKeyType();
}
