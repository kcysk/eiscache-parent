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
package net.zdsoft.cache.expiry;

/**
 * Expiration Policies
 * @author shenke
 * @since 17-9-3下午10:25
 */
public interface ExpiryPolicy {

    boolean expire(Duration duration, long creation);

    /**
     * 暂时不支持
     */
    Duration getAccessExpire();

    /**
     * 暂时不支持
     */
    Duration getUpdateExpire();

    Duration getCreateExpire();
}
