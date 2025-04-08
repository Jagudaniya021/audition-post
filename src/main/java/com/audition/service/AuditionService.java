/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.service;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;


/**
 * Handles all the service for AuditionController.
 */
public interface AuditionService {

    List<AuditionPost> getPosts(String title);

    AuditionPost getPostById(String postId);

    AuditionPost getPostWithComments(String postId);

    void postIdValidation(String postId);

    List<Comment> getComments(String postId);
}
