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
package net.zdsoft.cache.support;

import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.expiry.ExpiryPolicy;
import net.zdsoft.cache.expiry.TTLExpiryPolicy;
import net.zdsoft.cache.transfer.ByteTransfer;
import net.zdsoft.cache.transfer.ValueTransfer;

/**
 * @author shenke
 * @since 2017.09.26
 */
public class GlobalConfiguration implements Configuration {

    private ExpiryPolicy    defaultExpiry = new TTLExpiryPolicy(Duration.NEVER);
    private ValueTransfer   valueTransfer = new JSONValueTransfer();
    private ByteTransfer    byteTransfer  = new DefaultByteTransfer();

    @Override
    public ExpiryPolicy getExpiry() {
        return defaultExpiry;
    }

    @Override
    public ValueTransfer getValueTransfer() {
        return valueTransfer;
    }

    @Override
    public ByteTransfer getByteTransfer() {
        return byteTransfer;
    }

    @Override
    public Class<?> getKeyType() {
        return String.class;
    }
}
