package com.plexus.model.account;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class ProfileLogger {

    private String id;
    private String title;
    private String postid;
    private String comment_text;
    private String comment_type;
    private String userid;
    private String description;
    private String timestamp;
    private boolean isLike;
    private boolean isPost;
    private boolean isComment;
    private boolean isFollow;
    private boolean isStory;

    public ProfileLogger(String id, String title, String postid, String comment_text, String comment_type, String userid, String description, String timestamp, boolean isLike, boolean isPost, boolean isComment, boolean isFollow, boolean isStory) {
        this.id = id;
        this.title = title;
        this.postid = postid;
        this.comment_text = comment_text;
        this.comment_type = comment_type;
        this.userid = userid;
        this.description = description;
        this.timestamp = timestamp;
        this.isLike = isLike;
        this.isPost = isPost;
        this.isComment = isComment;
        this.isFollow = isFollow;
        this.isStory = isStory;
    }

    public ProfileLogger() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isStory() {
        return isStory;
    }

    public void setStory(boolean story) {
        isStory = story;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }
}
