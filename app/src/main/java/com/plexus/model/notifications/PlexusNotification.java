package com.plexus.model.notifications;

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

public class PlexusNotification {
    private String id;
    private String userid;
    private String text;
    private String postid;
    private boolean ispost;
    private boolean follower;
    private boolean comment;
    private boolean reaction;
    private boolean video;
    private boolean shared;
    private boolean notificationViewed;
    private boolean notificationRead;
    private String timestamp;

    public PlexusNotification(String id,
                              String userid,
                              String text,
                              String postid,
                              boolean ispost,
                              boolean follower,
                              boolean comment,
                              boolean reaction,
                              boolean video,
                              boolean notificationViewed,
                              boolean notificationRead,
                              String timestamp, boolean shared) {
        this.id = id;
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
        this.follower = follower;
        this.comment = comment;
        this.reaction = reaction;
        this.video = video;
        this.notificationViewed = notificationViewed;
        this.notificationRead = notificationRead;
        this.timestamp = timestamp;
        this.shared = shared;
    }

    public PlexusNotification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    public boolean isFollower() {
        return follower;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isReaction() {
        return reaction;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isShared() {
        return shared;
    }

    public boolean isNotificationViewed() {
        return notificationViewed;
    }

    public void setNotificationViewed(boolean notificationViewed) {
        this.notificationViewed = notificationViewed;
    }

    public boolean isNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }
}
