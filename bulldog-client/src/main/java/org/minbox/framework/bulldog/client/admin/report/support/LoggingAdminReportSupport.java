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

package org.minbox.framework.bulldog.client.admin.report.support;

import org.minbox.framework.bulldog.client.LoggingFactoryBean;
import org.minbox.framework.bulldog.client.MinBoxLoggingException;
import org.minbox.framework.bulldog.client.admin.report.LoggingAdminReport;
import org.minbox.framework.bulldog.client.cache.LoggingCache;
import org.minbox.framework.bulldog.common.LoggingClientNotice;
import org.minbox.framework.bulldog.common.MinBoxLog;
import org.minbox.framework.bulldog.common.response.ReportResponse;
import org.minbox.framework.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * ApiBoot Logging Admin Report Support
 *
 * @author 恒宇少年
 */
public class LoggingAdminReportSupport implements LoggingAdminReport, DisposableBean {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(LoggingAdminReportSupport.class);
    /**
     * Report Request Logging Uri
     */
    private static final String REPORT_LOG_URI = "/logging/report";
    private LoggingFactoryBean factoryBean;
    private RestTemplate restTemplate;

    public LoggingAdminReportSupport(LoggingFactoryBean factoryBean) {
        Assert.notNull(factoryBean, "The LoggingFactoryBean cannot be null.");
        this.factoryBean = factoryBean;
        this.restTemplate = factoryBean.getRestTemplate();
    }

    /**
     * Report Logs Interval
     * Recycle the request log when an exception is encountered
     *
     * @throws MinBoxLoggingException Logging Exception
     */
    @Override
    public void report() throws MinBoxLoggingException {
        if (ObjectUtils.isEmpty(factoryBean.getLoggingAdminDiscovery())) {
            logger.warn("Not set 【LoggingAdminDiscovery】in LoggingFactoryBean，don't invoke report request logs.");
            return;
        }
        List<MinBoxLog> logs = new ArrayList<>();
        LoggingCache loggingCache = factoryBean.getLoggingCache();
        Integer numberOfRequestLog = factoryBean.getNumberOfRequestLog();
        try {
            logs = loggingCache.getLogs(numberOfRequestLog);
            report(logs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (!ObjectUtils.isEmpty(logs)) {
                logs.stream().forEach(log -> loggingCache.cache(log));
            }
        }
    }

    /**
     * Report Logs To Admin
     * get number logs from cache
     * lookup a available api-boot-logging-admin service url
     * report request logs ro admin service
     * if admin use spring security, set restTemplate header basic auth info
     *
     * @param logs Request Logs
     * @throws MinBoxLoggingException Logging Exception
     */
    @Override
    public void report(List<MinBoxLog> logs) throws MinBoxLoggingException {
        if (ObjectUtils.isEmpty(logs)) {
            logger.warn("Don't have report request logs.");
            return;
        }
        if (ObjectUtils.isEmpty(factoryBean.getLoggingAdminDiscovery())) {
            logger.warn("Not set【LoggingAdminDiscovery】in LoggingFactoryBean，don't invoke report request logs.");
            return;
        }
        String adminServiceUrl = getAfterFormatAdminUrl();
        logger.debug("Report logging admin url：{}", adminServiceUrl);
        LoggingClientNotice notice = LoggingClientNotice.instance(
                factoryBean.getServiceId(), factoryBean.getServiceAddress(), factoryBean.getServicePort(), logs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String basicAuth = factoryBean.getLoggingAdminDiscovery().getBasicAuth();
        if (!ObjectUtils.isEmpty(basicAuth)) {
            headers.setBasicAuth(basicAuth);
        }
        HttpEntity<String> request = new HttpEntity(JsonUtils.toJsonString(notice), headers);
        ResponseEntity<ReportResponse> response =
                factoryBean.getRestTemplate().postForEntity(adminServiceUrl, request, ReportResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody().getStatus().equals(ReportResponse.SUCCESS)) {
            logger.debug("Report request logging successfully to admin.");
        } else {
            logger.error("Report request logging error to admin.");
        }
    }

    /**
     * Get After Format Admin URL
     *
     * @return ApiBoot Logging Admin URL
     */
    private String getAfterFormatAdminUrl() {
        // api boot admin service uri
        String adminServiceUri = factoryBean.getLoggingAdminDiscovery().lookup();
        // api boot admin service url
        return String.format("%s%s", adminServiceUri, REPORT_LOG_URI);
    }

    /**
     * Bean Destroy
     * When destroyed, report all request logs in the cache to admin
     *
     * @throws Exception exception
     */
    @Override
    public void destroy() throws Exception {
        // get all cache logs
        List<MinBoxLog> logs = factoryBean.getLoggingCache().getAll();
        // report to admin
        report(logs);
        logger.debug("The program is destroyed to execute the request log in the report cache to admin successfully.");
    }
}
