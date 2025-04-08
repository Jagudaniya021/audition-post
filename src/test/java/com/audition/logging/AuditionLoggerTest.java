/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.logging;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class AuditionLoggerTest {

    @Mock
    private Logger logger;
    @InjectMocks
    private AuditionLogger auditionLogger;

    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger(); // Initialize the AuditionLogger instance
    }

    @Test
    void infoLoggingTestWithMessage() {
        when(logger.isInfoEnabled()).thenReturn(true);
        String message = "Info message";
        auditionLogger.info(logger, message);
        verify(logger, times(1)).info(message);
    }

    @Test
    void infoLoggingTestWithMessageFAlse() {
        when(logger.isInfoEnabled()).thenReturn(false);
        String message = "Info message";
        auditionLogger.info(logger, message);
        verify(logger, times(0)).info(message);
    }

    @Test
    void infoLoggingTestWithMessageAndObject() {
        when(logger.isInfoEnabled()).thenReturn(true);
        String message = "Info message with object";
        Object object = new Object();
        auditionLogger.info(logger, message, object);
        verify(logger, times(1)).info(message, object);
    }

    @Test
    void infoLoggingTestWithMessageAndObjectFalse() {
        when(logger.isInfoEnabled()).thenReturn(false);
        String message = "Info message with object";
        Object object = new Object();
        auditionLogger.info(logger, message, object);
        verify(logger, times(0)).info(message, object);
    }

    @Test
    void debugLoggingTest() {
        when(logger.isDebugEnabled()).thenReturn(true);
        String message = "Debug message";
        auditionLogger.debug(logger, message);
        verify(logger, times(1)).debug(message);
    }

    @Test
    void debugLoggingTestFalse() {
        when(logger.isDebugEnabled()).thenReturn(false);
        String message = "Debug message";
        auditionLogger.debug(logger, message);
        verify(logger, times(0)).debug(message);
    }

    @Test
    void warnLoggingTest() {
        when(logger.isWarnEnabled()).thenReturn(true);
        String message = "Warn message";
        auditionLogger.warn(logger, message);
        verify(logger, times(1)).warn(message);
    }

    @Test
    void warnLoggingTestFalse() {
        when(logger.isWarnEnabled()).thenReturn(false);
        String message = "Warn message";
        auditionLogger.warn(logger, message);
        verify(logger, times(0)).warn(message);
    }

    @Test
    void errorLoggingTest() {
        when(logger.isErrorEnabled()).thenReturn(true);
        String message = "Error message";
        auditionLogger.error(logger, message);
        verify(logger, times(1)).error(message);
    }

    @Test
    void errorLoggingTestFalse() {
        when(logger.isErrorEnabled()).thenReturn(false);
        String message = "Error message";
        when(logger.isErrorEnabled()).thenReturn(false);
        auditionLogger.error(logger, message);
        verify(logger, times(0)).error(message);
    }

    @Test
    void logErrorWithExceptionTest() {
        when(logger.isErrorEnabled()).thenReturn(true);
        String message = "Error message with exception";
        Exception e = new Exception("Test exception");
        auditionLogger.logErrorWithException(logger, message, e);
        verify(logger, times(1)).error(message, e);
    }

    @Test
    void logErrorWithExceptionTestFalse() {
        when(logger.isErrorEnabled()).thenReturn(false);
        String message = "Error message with exception";
        Exception e = new Exception("Test exception");
        auditionLogger.logErrorWithException(logger, message, e);
        verify(logger, times(0)).error(message, e);
    }

    @Test
    void logHttpStatusCodeErrorTest() {
        when(logger.isErrorEnabled()).thenReturn(true);
        String message = "No Data Found";
        auditionLogger.logHttpStatusCodeError(logger, message, 404);
        verify(logger, times(1)).error("404 - No Data Found\n");
    }

    @Test
    void logHttpStatusCodeErrorTestFalse() {
        when(logger.isErrorEnabled()).thenReturn(false);
        String message = "No Data Found";
        auditionLogger.logHttpStatusCodeError(logger, message, 404);
        verify(logger, times(0)).error("404 - No Data Found\n");
    }

    @Test
    void logStandardProblemDetailTest() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setDetail("forbidden");
        problemDetail.setInstance(URI.create("http://example.com/resource/1234"));
        problemDetail.setType(URI.create("http://example.com/no-data-error"));
        Exception exception = new Exception("No Data Found");

        when(logger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logStandardProblemDetail(logger, problemDetail, exception);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(logger, times(1)).error(messageCaptor.capture(), throwableCaptor.capture());
    }

    @Test
    void logStandardProblemDetailTestFalse() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setDetail("forbidden");
        problemDetail.setInstance(URI.create("http://example.com/resource/1234"));
        problemDetail.setType(URI.create("http://example.com/no-data-error"));
        Exception exception = new Exception("No Data Found");

        when(logger.isErrorEnabled()).thenReturn(false);
        auditionLogger.logStandardProblemDetail(logger, problemDetail, exception);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(logger, times(0)).error(messageCaptor.capture(), throwableCaptor.capture());
    }
}
