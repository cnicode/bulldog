/*
 * Copyright [2019] [恒宇少年 - 于起宇]
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package org.minbox.framework.bulldog.client.span.support;


import org.minbox.framework.bulldog.client.MinBoxLoggingException;
import org.minbox.framework.bulldog.client.span.LogSpanIdGenerator;
import org.minbox.framework.bulldog.common.MinBoxLog;

import java.util.UUID;

/**
 * ApiBoot Logging Default Span
 * Use By Create New SpanId
 *
 * @author 恒宇少年
 */
public class DefaultLogSpanIdGenerator implements LogSpanIdGenerator {
    /**
     * Create New SpanId
     * Use random uuid as default spanId
     *
     * @return {@link MinBoxLog#getSpanId()}
     * @throws MinBoxLoggingException Exception
     */
    @Override
    public String createSpanId() throws MinBoxLoggingException {
        return UUID.randomUUID().toString();
    }
}
