/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
class AuditionControllerTest {

    @Mock
    private transient AuditionService auditionService;
    @InjectMocks
    private transient AuditionController auditionController;
    @Mock
    private transient AuditionPost auditionPost;
    @Mock
    private transient Comment comment;
    private List<Comment> commentList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, 1, "Comment Name", "Comment Email", "Comment Body");
        commentList.add(comment);
        auditionPost = new AuditionPost(1, 1, "Audition Post Title", "Post Body", commentList);
    }

    @Test
    public void getPostsWithFilterTest() {
        final List<AuditionPost> mockPosts = Collections.singletonList(auditionPost);
        when(auditionService.getPosts("Post Title")).thenReturn(mockPosts);
        ResponseEntity<List<AuditionPost>> response = auditionController.getPostsWithFilter("Post Title");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        verify(auditionService, times(1)).getPosts("Post Title");
    }

    @Test
    public void getPostByIdTest() {
        when(auditionService.getPostById("1")).thenReturn(auditionPost);
        ResponseEntity<AuditionPost> response = auditionController.getPosts("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(auditionService, times(1)).getPostById("1");
    }

    @Test
    public void getPostWithCommentsTest() {
        when(auditionService.getPostWithComments("1")).thenReturn(auditionPost);
        ResponseEntity<AuditionPost> response = auditionController.getPostWithComments("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(auditionService, times(1)).getPostWithComments("1");
    }

    @Test
    public void getCommentByIdTest() {
        when(auditionService.getComments("1")).thenReturn(commentList);
        ResponseEntity<List<Comment>> response = auditionController.getCommentById("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(auditionService, times(1)).getComments("1");
    }
}
