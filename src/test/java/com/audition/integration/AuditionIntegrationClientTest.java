/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class AuditionIntegrationClientTest {

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String JSON_PLACEHOLDER_POST_FILTER_PATH = "/posts";
    public static final String JSON_PLACEHOLDER_POST_ID_PATH = "/posts/";

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    JsonNode jsonNode;


    private transient String baseUrl = "https://jsonplaceholder.typicode.com";

    private transient AuditionPost auditionPost;
    private transient Comment comment;
    private List<Comment> commentList = new ArrayList<>();
    private List<AuditionPost> auditionPostList = new ArrayList<>();

    public static final String ERROR_PARSING_ERROR_RESPONSE = "Error parsing error response message from external API.";
    public static final String NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING = "No comments found for postId matching: ";
    public static final String NO_POSTS_FOUND_FOR_ID_MATCHING = "No posts found for id matching: ";
    public static final String NO_POSTS_FOUND = "No posts found.";


    @BeforeEach
    public void setUp() throws Exception {
        comment = new Comment(1, 1, "Comment Name", "Comment Email", "Comment Body");
        commentList.add(comment);
        auditionPost = new AuditionPost(1, 1, "Audition Post Title", "Post Body", commentList);
        auditionPostList.add(auditionPost);
        try {
            Field baseUrlField = AuditionIntegrationClient.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(auditionIntegrationClient, "https://jsonplaceholder.typicode.com");
            baseUrlField.setAccessible(false);
            this.baseUrl = "https://jsonplaceholder.typicode.com";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getPostsTestsuccess() {
        String expectedPath = baseUrl + JSON_PLACEHOLDER_POST_FILTER_PATH;
        ResponseEntity<List<AuditionPost>> responseEntity = new ResponseEntity<>(auditionPostList, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedPath), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<AuditionPost>>>any())).thenReturn(responseEntity);

        List<AuditionPost> result = auditionIntegrationClient.getPosts();

        assertNotNull(result);
        assertEquals(auditionPostList.size(), result.size());
        verify(restTemplate, times(1)).exchange(eq(expectedPath), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<List<AuditionPost>>>any());
    }


    @Test
    void getPostsTestNotFoundException() {
        String expectedPath = baseUrl + JSON_PLACEHOLDER_POST_FILTER_PATH;
        when(restTemplate.exchange(eq(expectedPath), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<AuditionPost>>>any())).thenThrow(
            getRestClientResponseExceptionNoDataFound());

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());

        assertEquals(NO_POSTS_FOUND, exception.getMessage());
        verify(restTemplate, times(1)).exchange(eq(expectedPath), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<List<AuditionPost>>>any());
    }

    @Test
    void getPostByIdTestSuccess() {
        String expectedPath = baseUrl + JSON_PLACEHOLDER_POST_ID_PATH + "1";
        ResponseEntity<AuditionPost> responseEntity = new ResponseEntity<>(auditionPost, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedPath), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any())).thenReturn(responseEntity);

        AuditionPost result = auditionIntegrationClient.getPostById("1");

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(expectedPath), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any());
    }

    @Test
    void getPostByIdTestNotFoundException() {
        String expectedPath = baseUrl + JSON_PLACEHOLDER_POST_ID_PATH + "1";
        when(restTemplate.exchange(eq(expectedPath), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any())).thenThrow(
            getRestClientResponseExceptionNoDataFound());

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("1"));

        assertEquals(NO_POSTS_FOUND_FOR_ID_MATCHING + "1", exception.getMessage());
        verify(restTemplate, times(1)).exchange(eq(expectedPath), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any());
    }

    @Test
    void getPostWithCommentsTestSuccess() {
        String expectedPathPost = baseUrl + JSON_PLACEHOLDER_POST_ID_PATH + "1";
        String expectedPathComment = baseUrl + "/posts/1/comments";
        ResponseEntity<AuditionPost> responseEntityPost = new ResponseEntity<>(auditionPost, HttpStatus.OK);
        ResponseEntity<List<Comment>> responseEntityComments = new ResponseEntity<>(commentList, HttpStatus.OK);

        when(restTemplate.exchange(eq(expectedPathComment), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Comment>>>any())).thenReturn(responseEntityComments);
        when(restTemplate.exchange(eq(expectedPathPost), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any())).thenReturn(responseEntityPost);

        AuditionPost result = auditionIntegrationClient.getPostWithComments("1");

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(eq(expectedPathPost), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<AuditionPost>>any());
        verify(restTemplate, times(1)).exchange(eq(expectedPathComment), eq(HttpMethod.GET), eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<List<Comment>>>any());
    }

    @Test
    void getCommentsTestNotFoundException() {
        String expectedPath = baseUrl + "/comments?postId=1";
        when(restTemplate.exchange(eq(expectedPath), eq(HttpMethod.GET), isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Comment>>>any())).thenThrow(
            getRestClientResponseExceptionNoDataFound());

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getComments("1"));

        assertEquals(NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING + "1", exception.getMessage());
    }

    private static RestClientResponseException getRestClientResponseExceptionNoDataFound() {
        RestClientResponseException restClientResponseException = new RestClientResponseException("Not Found",
            HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null);
        return restClientResponseException;
    }

    private static RestClientResponseException getRestClientResponseExceptionServerError() {
        RestClientResponseException restClientResponseException = new RestClientResponseException(
            "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, null, null);
        return restClientResponseException;
    }
}
