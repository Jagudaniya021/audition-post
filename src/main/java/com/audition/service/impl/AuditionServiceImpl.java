/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.service.impl;

import com.audition.common.constant.AuditionPostConstants;
import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of AuditionService.
 */
@Service
public class AuditionServiceImpl implements AuditionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditionServiceImpl.class);

    @Autowired
    private transient AuditionIntegrationClient auditionIntegrationClient;

    /**
     * Retrieve all post with title filter if given else returns all posts.
     *
     * @param title - post title.
     * @return list of audition posts.
     */
    @Override
    public List<AuditionPost> getPosts(final String title) {
        final List<AuditionPost> list = auditionIntegrationClient.getPosts();
        // Check if the list is empty
        if (CollectionUtils.isEmpty(list)) {
            throw new SystemException(AuditionPostConstants.NO_POSTS_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value());
        }

        // If no title is provided, return the list as is
        if (StringUtils.isEmpty(title)) {
            return list;
        }
        LOGGER.debug("In AuditionServiceImpl.getPosts - Filtering posts that contain title: {}", title);

        // Filter AuditionPosts that contains title as given in method parameter
        final List<AuditionPost> filteredPosts = list.stream().filter(post -> post.getTitle().contains(title))
            .collect(Collectors.toList());

        // If no posts match, throw an exception
        if (CollectionUtils.isEmpty(filteredPosts)) {
            throw new SystemException("No posts found where the title contains: " + title,
                HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value());
        }
        return filteredPosts;
    }

    /**
     * Retrieve audition posts by postId.
     *
     * @param postId - audition post id.
     * @return audition post object.
     */
    @Override
    public AuditionPost getPostById(final String postId) {
        final AuditionPost auditionPost = auditionIntegrationClient.getPostById(postId);
        // throw exception is there are no posts
        if (Objects.isNull(auditionPost)) {
            throw new SystemException(AuditionPostConstants.NO_POSTS_FOUND_FOR_ID_MATCHING + postId,
                HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value());
        }
        return auditionPost;
    }

    /**
     * Retrieve post and it's all comments.
     *
     * @param postId - audition post id.
     * @return audition post object.
     */
    @Override
    public AuditionPost getPostWithComments(final String postId) {
        return auditionIntegrationClient.getPostWithComments(postId);
    }


    /**
     * Retrieve all comments for given postId.
     *
     * @param postId - audition post id.
     * @return list of comments.
     */
    @Override
    public List<Comment> getComments(final String postId) {
        final List<Comment> commentList = auditionIntegrationClient.getComments(postId);
        // throw exception is there are no comments
        if (CollectionUtils.isEmpty(commentList)) {
            throw new SystemException(AuditionPostConstants.NO_COMMENTS_FOUND_FOR_POST_ID_MATCHING + postId,
                HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value());
        }
        return commentList;
    }

    /**
     * validate postId should be valid positive number.
     *
     * @param postId - audition post id.
     */
    @Override
    public void postIdValidation(final String postId) {
        LOGGER.debug("In AuditionServiceImpl.postIdValidation - Validating postId: {}", postId);
        try {
            if (Integer.parseInt(postId) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new SystemException("Invalid PostId format. Must be a positive integer",
                HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value(), e);
        }
    }

}
