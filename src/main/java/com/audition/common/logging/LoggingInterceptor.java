/* * Copyright 2025 XYZ, Inc. *  Licensed under the XYZ License, Version 1.0 *  you may not use this file except in compliance with the License. *  You may obtain a copy of the License at * *   http://www.xyz.org/licenses/LICENSE-1.0 */

package com.audition.common.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Logging interceptor for http client request/response.
 */
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    private final transient AuditionLogger auditionLogger;
    private final transient boolean isInterceptorEnabled;

    public LoggingInterceptor(final AuditionLogger auditionLogger,
        @Value("${logging.interceptor.enabled}") final boolean isInterceptorEnabled) {
        this.auditionLogger = auditionLogger;
        this.isInterceptorEnabled = isInterceptorEnabled;
    }


    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        logHttpRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        logHttpResponse(response);
        return response;
    }

    /**
     * Log request.
     *
     * @param request - http request.
     * @param body    - request body.
     */
    private void logHttpRequest(final HttpRequest request, final byte[] body) {
        if (isInterceptorEnabled) {
            final String requestLog = String.format(
                "%n--------------------HTTP REQUEST--------------------%n" + "Method: %s%n" + "URI: %s%n"
                    + "Request Header: %s%n" + "Request Body: %s%n", request.getMethod(), request.getURI(),
                request.getHeaders(), Arrays.toString(body));
            auditionLogger.info(LOGGER, requestLog);
        }
    }

    /**
     * Log response.
     *
     * @param response - http response.
     * @throws IOException - IO exception.
     */
    private void logHttpResponse(final ClientHttpResponse response) throws IOException {
        if (isInterceptorEnabled) {
            final String responseLog = String.format(
                "%n--------------------HTTP RESPONSE--------------------%n" + "Status Code: %s%n" + "Status Desc: %s%n"
                    + "Response Header: %s%n" + "Response Body: %s%n", response.getStatusCode(),
                response.getStatusText(), response.getHeaders(),
                new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
            auditionLogger.info(LOGGER, responseLog);
        }
    }
}