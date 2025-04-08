/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationException authException;
    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    void commenceT() throws IOException {
        String servletPath = "/test-path";
        String exceptionMessage = "Unauthorized access";

        when(request.getServletPath()).thenReturn(servletPath);
        when(authException.getMessage()).thenReturn(exceptionMessage);
        when(response.getWriter()).thenReturn(printWriter);

        final ArgumentCaptor<String> responseBodyCaptor = ArgumentCaptor.forClass(String.class);
        customAuthenticationEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValueAsString(anyMap());
        verify(printWriter).write(responseBodyCaptor.capture());
    }
}

