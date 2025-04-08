/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import com.audition.common.constant.AuditionPostConstants;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Common class to modify response header.
 */
@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    /**
     * Set OpenTelemetry trade-span ids in response header for each http request.
     *
     * @param request     - http request.
     * @param response    - http response.
     * @param filterChain - filter chain.
     * @throws ServletException - unexpected exception in servlet processing.
     * @throws IOException      - IO exception.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain filterChain) throws ServletException, IOException {
        final Span currentSpan = Span.current();
        final SpanContext spanContext = currentSpan.getSpanContext();
        if (spanContext.isValid()) {
            response.addHeader(AuditionPostConstants.TRACE_ID, spanContext.getTraceId());
            response.addHeader(AuditionPostConstants.SPAN_ID, spanContext.getSpanId());
        }
        filterChain.doFilter(request, response);
    }
}