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

package org.minbox.framework.bulldog.common;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ApiBoot Logging Client Notice Object
 *
 * @author 恒宇少年
 */
@Data
public class LoggingClientNotice implements Serializable {
    /**
     * Client Service Id
     */
    private String clientServiceId;
    /**
     * Client Service Ip Address
     */
    private String clientServiceIp;
    /**
     * Client Service Port
     */
    private Integer clientServicePort;
    /**
     * Report Time Millis
     */
    private Long reportTimeMillis = System.currentTimeMillis();
    /**
     * ApiBoot Logging Request Log
     */
    private List<MinBoxLog> loggers = new ArrayList<>();

    /**
     * Create new {@link LoggingClientNotice} instance
     *
     * @param clientServiceId   {@link #clientServiceId}
     * @param clientServiceIp   {@link #clientServiceIp}
     * @param clientServicePort {@link #clientServicePort}
     * @param loggers           {@link #loggers}
     * @return {@link LoggingClientNotice}
     */
    public static LoggingClientNotice instance(
            String clientServiceId, String clientServiceIp, Integer clientServicePort, List<MinBoxLog> loggers) {
        LoggingClientNotice notice = new LoggingClientNotice();
        notice.setClientServiceId(clientServiceId);
        notice.setClientServiceIp(clientServiceIp);
        notice.setClientServicePort(clientServicePort);
        notice.setLoggers(loggers);
        return notice;
    }
}
