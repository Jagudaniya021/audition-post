/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all the requests for audition post.
 */
@OpenAPIDefinition(info = @Info(title = "Audition Post", version = "v1", description = "Audition Post Data Retrieval"), tags = {
    @Tag(name = "Audition Post", description = "Operations related to Audition Post")})
@RestController
public class AuditionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditionController.class);

    @Autowired
    private transient AuditionService auditionService;

    /**
     * Returns all audition posts filtering with title if provided else returns all posts.
     *
     * @param title - audition title.
     * @return list of auditionPost.
     */
    @Operation(summary = "Get a post with optional filtering by title")
    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<AuditionPost>> getPostsWithFilter(
        @Parameter(description = "title to filter post", required = false) @RequestParam(required = false) final String title) {

        LOGGER.debug("Entering method AuditionController.getPostsWithFilter with title: {}", title);

        //Added title to filter if passed. It will return post that contain input string in title
        final List<AuditionPost> auditionPostList = auditionService.getPosts(title);

        LOGGER.debug("Exiting method AuditionController.getPostsWithFilter with title: {}", title);
        return ResponseEntity.status(HttpStatus.OK).body(auditionPostList);
    }

    /**
     * Returns all audition posts for given postId.
     *
     * @param postId - audition post id.
     * @return audition post.
     */
    @Operation(summary = "Get post by id")
    @GetMapping(value = "/posts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<AuditionPost> getPosts(
        @Parameter(description = "Post Id") @PathVariable("id") final String postId) {

        LOGGER.debug("Entering method AuditionController.getPosts with postId: {}", postId);

        auditionService.postIdValidation(postId);
        final AuditionPost auditionPosts = auditionService.getPostById(postId);

        LOGGER.debug("Exiting method AuditionController.getPosts with postId: {}", postId);
        return ResponseEntity.ok(auditionPosts);
    }

    /**
     * Returns audition posts for given postId with all comments.
     *
     * @param postId - audition post id.
     * @return audition post comments.
     */
    @Operation(summary = "Get all comments with post for given post id")
    @GetMapping(value = "/posts/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<AuditionPost> getPostWithComments(
        @Parameter(description = "Post Id") @PathVariable("id") final String postId) {

        LOGGER.debug("Entering method AuditionController.getPostWithComments with postId: {}", postId);

        auditionService.postIdValidation(postId);
        final AuditionPost auditionPostComment = auditionService.getPostWithComments(postId);

        LOGGER.debug("Exiting method AuditionController.getPostWithComments with postId: {}", postId);
        return ResponseEntity.ok(auditionPostComment);
    }

    /**
     * Returns all comments for given postId.
     *
     * @param postId - audition post id.
     * @return list of comments.
     */
    @Operation(summary = "Get all comments by id")
    @GetMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getCommentById(
        @Parameter(description = "Post Id") @RequestParam("postId") final String postId) {

        LOGGER.debug("Exiting method AuditionController.getCommentById with postId: {}", postId);

        auditionService.postIdValidation(postId);
        final List<Comment> commentList = auditionService.getComments(postId);

        LOGGER.debug("Exiting method AuditionController.getCommentById with postId: {}", postId);
        return ResponseEntity.status(HttpStatus.OK).body(commentList);
    }
}
