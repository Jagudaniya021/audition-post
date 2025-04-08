/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.logging;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import com.audition.common.logging.LoggingInterceptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class LoggingInterceptorTest {

    @Mock
    private AuditionLogger auditionLogger;
    @Mock
    private ClientHttpRequestExecution clientHttpRequestExecution;
    @Mock
    private ClientHttpResponse clientHttpResponse;
    @Mock
    private HttpRequest httpRequest;
    private LoggingInterceptor loggingInterceptor;
    private transient byte[] body;


    @BeforeEach
    void setUp() {
        body = "body".getBytes(UTF_8);
    }

    @Test
    void interceptTestLoggingEnabled() throws IOException, URISyntaxException {
        when(clientHttpRequestExecution.execute(any(), any())).thenReturn(clientHttpResponse);

        // Mock the HttpRequest and ClientHttpResponse
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(httpRequest.getURI()).thenReturn(new URI("http://localhost/test"));
        when(httpRequest.getHeaders()).thenReturn(new HttpHeaders());

        // Mock the response body
        String responseBody = "response body";
        when(clientHttpResponse.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(clientHttpResponse.getStatusText()).thenReturn("OK");
        when(clientHttpResponse.getHeaders()).thenReturn(new HttpHeaders());
        when(clientHttpResponse.getBody()).thenReturn(new ByteArrayInputStream(responseBody.getBytes(UTF_8)));

        loggingInterceptor = new LoggingInterceptor(auditionLogger, Boolean.TRUE);
        loggingInterceptor.intercept(httpRequest, body, clientHttpRequestExecution);
        verify(auditionLogger, times(2)).info(any(), anyString());
    }

    @Test
    void interceptTestLoggingDiabled() throws IOException, URISyntaxException {
        when(clientHttpRequestExecution.execute(any(), any())).thenReturn(clientHttpResponse);
        loggingInterceptor = new LoggingInterceptor(auditionLogger, Boolean.FALSE);
        loggingInterceptor.intercept(httpRequest, body, clientHttpRequestExecution);
        verify(auditionLogger, times(0)).info(any(), anyString());
    }
}
