/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.impl.AuditionServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class AuditionServiceImplTest {

    @Mock
    private transient AuditionIntegrationClient auditionIntegrationClient;
    @InjectMocks
    private transient AuditionServiceImpl auditionService;

    private transient AuditionPost auditionPost;
    private transient Comment comment;
    private List<Comment> commentList = new ArrayList<>();
    private List<AuditionPost> auditionPostList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, 1, "Comment Name", "Comment Email", "Comment Body");
        commentList.add(comment);
        auditionPost = new AuditionPost(1, 1, "Audition Post Title", "Post Body", commentList);
        auditionPostList.add(auditionPost);
    }


    @Test
    public void getPostsTestWithTitle() {
        when(auditionIntegrationClient.getPosts()).thenReturn(auditionPostList);
        List<AuditionPost> result = auditionService.getPosts("Title");
        assertNotNull(result);
        assertTrue(result.get(0).getTitle().contains("Title"));
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    public void getPostsTestWithNoTitle() {
        when(auditionIntegrationClient.getPosts()).thenReturn(auditionPostList);
        List<AuditionPost> result = auditionService.getPosts(null);
        assertNotNull(result);
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    public void getPostsTestWithNoMatchingTitle() {
        when(auditionIntegrationClient.getPosts()).thenReturn(auditionPostList);
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.getPosts("Title2");
        });
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    public void getPostsTestWithNoDataException() {
        when(auditionIntegrationClient.getPosts()).thenReturn(Collections.EMPTY_LIST);
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.getPosts("Title2");
        });
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    public void getPostByIdTest() {
        when(auditionIntegrationClient.getPostById("1")).thenReturn(auditionPost);
        AuditionPost result = auditionService.getPostById("1");
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(auditionIntegrationClient, times(1)).getPostById(Mockito.anyString());
    }

    @Test
    public void getPostByIdTestWithNoDataException() {
        when(auditionIntegrationClient.getPostById("1")).thenReturn(null);
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.getPostById("1");
        });
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(auditionIntegrationClient, times(1)).getPostById(Mockito.anyString());
    }

    @Test
    public void getPostWithCommentsTest() {
        when(auditionIntegrationClient.getPostWithComments(Mockito.anyString())).thenReturn(auditionPost);
        AuditionPost result = auditionService.getPostWithComments(Mockito.anyString());
        assertNotNull(result);
        assertEquals(auditionPost.getId(), result.getId());
        verify(auditionIntegrationClient, times(1)).getPostWithComments(Mockito.anyString());
    }

    @Test
    public void getCommentsTest() {
        when(auditionIntegrationClient.getComments(Mockito.anyString())).thenReturn(commentList);
        List<Comment> result = auditionService.getComments(Mockito.anyString());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(auditionIntegrationClient, times(1)).getComments(Mockito.anyString());
    }

    @Test
    public void getCommentsTestWithNoDataFound() {
        when(auditionIntegrationClient.getComments(Mockito.anyString())).thenReturn(Collections.EMPTY_LIST);
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.getComments("1");
        });
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
        verify(auditionIntegrationClient, times(1)).getComments(Mockito.anyString());
    }

    @Test
    public void postIdValidationTestWithValidPostId() {
        auditionService.postIdValidation("1");
        // No need for additional verifications as we expect no exception
    }

    @Test
    public void postIdValidationTestWithInvalidPostId() {
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.postIdValidation("abc");
        });
        assertEquals("Invalid PostId format. Must be a positive integer", exception.getMessage());
    }

    @Test
    public void postIdValidationTestWithNagativePostId() {
        SystemException exception = assertThrows(SystemException.class, () -> {
            auditionService.postIdValidation("-1");
        });
        assertEquals("Invalid PostId format. Must be a positive integer", exception.getMessage());
    }
}
