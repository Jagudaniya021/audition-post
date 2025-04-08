/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.common.constant;

import java.text.MessageFormat;

/**
 * All required constant values for audition post.
 */
public final class AuditionPostConstants {

    // Public constants for API endpoints
    public static final String JSON_PLACEHOLDER_POST_FILTER_PATH = "/posts";
    public static final String JSON_PLACEHOLDER_POST_ID_PATH = "/posts/";
    public static final String JSON_PLACEHOLDER_POST_WITH_COMMENTS_PATH = "/posts/{0}/comments";
    public static final String JSON_PLACEHOLDER_COMMENTS_PATH = "/comments";

    // OpenTelemetry
    public static final String TRACE_ID = "X-OpenTelemetry-TraceId";
    public static final String SPAN_ID = "X-OpenTelemetry-SpanId";

    //Error Messages
    public static final String ERROR_PARSING_ERROR_RESPONSE = "Error parsing error response message from external API.";
    public static final String NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING = "No comments found for postId matching: ";
    public static final String NO_POSTS_FOUND_FOR_ID_MATCHING = "No posts found for id matching: ";
    public static final String NO_POSTS_FOUND = "No posts found.";

    private AuditionPostConstants() {
    }

    public static String getJsonPlaceholderPostWithComment(final String postId) {
        return MessageFormat.format(JSON_PLACEHOLDER_POST_WITH_COMMENTS_PATH, postId);
    }
}
