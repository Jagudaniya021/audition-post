/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audition.common.logging.LoggingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class WebServiceConfigurationTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(LOWER_CAMEL_CASE).setSerializationInclusion(NON_EMPTY)
        .disable(WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private LoggingInterceptor loggingInterceptor;
    @InjectMocks
    private WebServiceConfiguration webServiceConfiguration;

    @Test
    void objectMapperBeanCreation() {
        ObjectMapper mapper = webServiceConfiguration.objectMapper();
        assertNotNull(mapper);
        assertEquals("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).toPattern());
        assertEquals(false, mapper.getDeserializationConfig().isEnabled(FAIL_ON_UNKNOWN_PROPERTIES));
        assertEquals(LOWER_CAMEL_CASE, mapper.getSerializationConfig().getPropertyNamingStrategy());
        assertEquals(NON_EMPTY, mapper.getSerializationConfig().getSerializationInclusion());
        assertEquals(false, mapper.getSerializationConfig().isEnabled(WRITE_DATES_AS_TIMESTAMPS));
    }

    @Test
    void restTemplateBeanCreation() {
        RestTemplate restTemplate = webServiceConfiguration.restTemplate(restTemplateBuilder);
        assertNotNull(restTemplate);
    }

    @Test
    void createClientFactoryBean_usingReflection() throws Exception {
        Method createClientFactoryMethod = WebServiceConfiguration.class.getDeclaredMethod("createClientFactory");
        // Bypass private access
        createClientFactoryMethod.setAccessible(true);
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) createClientFactoryMethod.invoke(
            webServiceConfiguration);
        assertNotNull(factory);
        // Reset access modifier
        createClientFactoryMethod.setAccessible(false);
    }
}