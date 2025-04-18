/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.common.exception;

import java.io.Serial;
import lombok.Getter;

/**
 * Common System Exception.
 */
@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5876728854007114881L;

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private Integer statusCode;
    private String title;
    private String detail;

    public SystemException() {
        super();
    }

    public SystemException(final String message) {
        super(message);
        this.title = DEFAULT_TITLE;
    }

    public SystemException(final String message, final Integer errorCode) {
        super(message);
        this.title = DEFAULT_TITLE;
        this.statusCode = errorCode;
    }

    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
        this.title = DEFAULT_TITLE;
    }

    public SystemException(final String detail, final String title, final Integer errorCode) {
        super(detail);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    public SystemException(final String detail, final String title, final Throwable exception) {
        super(detail, exception);
        this.title = title;
        this.statusCode = 500;
        this.detail = detail;
    }

    public SystemException(final String detail, final Integer errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = DEFAULT_TITLE;
        this.detail = detail;
    }

    public SystemException(final String detail, final String title, final Integer errorCode,
        final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }
}
