/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.integration;

import com.audition.common.constant.AuditionPostConstants;
import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles API integration for audition post.
 */
@Component
public class AuditionIntegrationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditionIntegrationClient.class);

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient ObjectMapper objectMapper;

    @Value("${spring.config.base-path}")
    private transient String baseUrl;


    /**
     * Integrate https://jsonplaceholder.typicode.com/posts - get all post/filter by title.
     *
     * @return list of auditionPost.
     */
    public List<AuditionPost> getPosts() {
        final String path = createEndpoint(baseUrl, AuditionPostConstants.JSON_PLACEHOLDER_POST_FILTER_PATH);
        LOGGER.debug("In AuditionIntegrationClient.getPosts calling: {}", path);
        ResponseEntity<List<AuditionPost>> response;
        try {
            response = restTemplate.exchange(path, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
            return response.getBody();
        } catch (final RestClientResponseException e) {
            throw processException(e, AuditionPostConstants.NO_POSTS_FOUND);
        }
    }

    /**
     * Integrate https://jsonplaceholder.typicode.com/posts/{id}  - get all post  by post id.
     *
     * @param postId - audition post id.
     * @return auditionPost object.
     */
    public AuditionPost getPostById(final String postId) {
        final String path = createEndpoint(baseUrl, AuditionPostConstants.JSON_PLACEHOLDER_POST_ID_PATH.concat(postId));
        LOGGER.debug("In AuditionIntegrationClient.getPostById calling: {}", path);
        try {
            final ResponseEntity<AuditionPost> response = restTemplate.exchange(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
            return response.getBody();
        } catch (final RestClientResponseException e) {
            throw processException(e, AuditionPostConstants.NO_POSTS_FOUND_FOR_ID_MATCHING + postId);
        }
    }

    /**
     * Integrate https://jsonplaceholder.typicode.com/posts/{postId}/comments  - get post with all comments by postId.
     *
     * @param postId - audition post id.
     * @return auditionPost object.
     */
    public AuditionPost getPostWithComments(final String postId) {
        final String path = createEndpoint(baseUrl, AuditionPostConstants.getJsonPlaceholderPostWithComment(postId));
        LOGGER.debug("In AuditionIntegrationClient.getPostWithComments calling: {}", path);
        try {
            final AuditionPost post = getPostById(postId);

            // If the post is null, throw an exception before using it.
            if (Objects.isNull(post)) {
                throw new SystemException(AuditionPostConstants.NO_POSTS_FOUND_FOR_ID_MATCHING + postId,
                    HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value());
            }
            // get comments.
            final ResponseEntity<List<Comment>> response = restTemplate.exchange(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

            // If the comment list is null, throw an exception.
            if (Objects.isNull(response) || Objects.isNull(response.getBody()) || CollectionUtils.isEmpty(
                response.getBody())) {
                throw new SystemException(AuditionPostConstants.NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING + postId,
                    HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value());
            }
            post.setComments(response.getBody());
            return post;
        } catch (final RestClientResponseException e) {
            throw processException(e, AuditionPostConstants.NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING + postId);

        }
    }

    /**
     * Integrate https://jsonplaceholder.typicode.com/comments?postID=id  - get all comments by postId.
     *
     * @param postId - audition post id.
     * @return list of comment.
     */
    public List<Comment> getComments(final String postId) {
        final HashMap<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("postId", postId);
        final String path = createEndpoint(baseUrl, AuditionPostConstants.JSON_PLACEHOLDER_COMMENTS_PATH,
            queryParamMap);
        LOGGER.debug("In AuditionIntegrationClient.getComments calling: {}", path);
        try {
            final ResponseEntity<List<Comment>> response = restTemplate.exchange(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
            return response.getBody();
        } catch (final RestClientResponseException e) {
            throw processException(e, AuditionPostConstants.NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING + postId);
        }
    }

    /**
     * Common code that handles exception.
     *
     * @param e            - exception from http request/response.
     * @param errorMessage - error message.
     * @return SystemException - custom exception.
     */
    private SystemException processException(final RestClientResponseException e, final String errorMessage) {
        LOGGER.debug("Exception Occured: {}", e);
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SystemException(errorMessage, HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value());
        } else {
            try {
                // fetch exact error message from external API and giv back in response so no error message is lost.
                final String responseBody = e.getResponseBodyAsString();
                final JsonNode jsonNode = objectMapper.readTree(responseBody);
                final String error = jsonNode.has("message") ? jsonNode.get("message").asText() : responseBody;
                throw new SystemException(error, e.getStatusText(), e.getStatusCode().value());
            } catch (IOException ex) {
                return new SystemException(AuditionPostConstants.ERROR_PARSING_ERROR_RESPONSE, ex);
            }
        }
    }

    /**
     * Retrieve endpoints URL withput queryParams.
     *
     * @param baseURL - baseURL.
     * @param path    - path to concate.
     * @return string URL.
     */
    public String createEndpoint(final String baseURL, final String path) {
        final String endpoint = UriComponentsBuilder.fromHttpUrl(baseURL.concat(path)).toUriString();
        return URLDecoder.decode(endpoint, StandardCharsets.UTF_8);
    }

    /**
     * Retrieve endpoint URL with queryParams.
     *
     * @param baseURL     - base URL.
     * @param path        - URL path.
     * @param queryParams - map of query parameters.
     * @return endpoint.
     */
    public String createEndpoint(final String baseURL, final String path, final Map<String, String> queryParams) {
        String endpoint;
        // set query parameter if we have.
        if (queryParams != null && !queryParams.isEmpty()) {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseURL.concat(path));
            for (final Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            endpoint = builder.toUriString();
        } else {
            endpoint = UriComponentsBuilder.fromHttpUrl(baseURL.concat(path)).toUriString();
        }
        return URLDecoder.decode(endpoint, StandardCharsets.UTF_8);
    }
}
