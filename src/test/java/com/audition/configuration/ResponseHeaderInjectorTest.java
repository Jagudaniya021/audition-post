/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class ResponseHeaderInjectorTest {

    public static final String TRACE_ID = "X-OpenTelemetry-TraceId";
    public static final String SPAN_ID = "X-OpenTelemetry-SpanId";

    // Subclass to expose protected method
    private static class ResponseHeaderInjectorTestable extends ResponseHeaderInjector {

        public void doFilterInternalTest(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
            super.doFilterInternal(request, response, filterChain);
        }
    }

    @Mock
    private ResponseHeaderInjectorTestable responseHeaderInjector;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Span currentSpan;
    @Mock
    private SpanContext spanContext;

    @BeforeEach
    void setUp() {
        responseHeaderInjector = new ResponseHeaderInjectorTestable();
    }

    @Test
    void headerInjectionTestWithTraceAndSpanIds() throws ServletException, IOException {
        try (MockedStatic<Span> mockedSpan = Mockito.mockStatic(Span.class)) {
            mockedSpan.when(Span::current).thenReturn(currentSpan);
            when(currentSpan.getSpanContext()).thenReturn(spanContext);
            when(spanContext.isValid()).thenReturn(true);
            when(spanContext.getTraceId()).thenReturn("123");
            when(spanContext.getSpanId()).thenReturn("456");

            responseHeaderInjector.doFilterInternalTest(request, response, filterChain);

            verify(response).addHeader(TRACE_ID, "123");
            verify(response).addHeader(SPAN_ID, "456");
            verify(filterChain).doFilter(request, response);
        }
    }
}
