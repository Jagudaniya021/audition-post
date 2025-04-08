/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of AuthenticationEntryPoint in case of unauthorized exception.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String PATH = "path";

    private final transient ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * response in case of unauthorized exception.
     *
     * @param request       - http servlet request.
     * @param response      - http servlet response
     * @param authException - unauthorized exception.
     * @throws IOException - IO exception.
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
        final AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final Map<String, Object> errorResponse = new ConcurrentHashMap<>();
        errorResponse.put(ERROR, UNAUTHORIZED);
        errorResponse.put(MESSAGE, authException.getMessage());
        errorResponse.put(PATH, request.getServletPath());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
