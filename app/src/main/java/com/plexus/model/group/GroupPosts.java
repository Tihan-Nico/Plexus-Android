package com.plexus.model.group;

public class GroupPosts {

    private String postid, shared_postid;
    private String postimage, videoURL, audioURL;
    private String description;
    private String publisher, shared_userid;
    private String timestamp;
    private String groupID;
    private boolean shared;
    private String type;
    private boolean ken_burns_effect;
    private boolean downloadEnabled;
    private boolean screenshotEnabled;

    public GroupPosts(
            String postid,
            String postimage,
            String videoURL,
            String description,
            String publisher,
            String timestamp,
            String groupID,
            boolean shared,
            String audioURL,
            String type,
            String publisher_shared,
            String shared_postid,
            boolean ken_burns_effect,
            boolean downloadEnabled,
            boolean screenshotEnabled) {
        this.postid = postid;
        this.shared_postid = shared_postid;
        this.postimage = postimage;
        this.videoURL = videoURL;
        this.audioURL = audioURL;
        this.description = description;
        this.publisher = publisher;
        this.shared_userid = publisher_shared;
        this.timestamp = timestamp;
        this.groupID = groupID;
        this.shared = shared;
        this.type = type;
        this.ken_burns_effect = ken_burns_effect;
        this.screenshotEnabled = screenshotEnabled;
        this.downloadEnabled = downloadEnabled;
    }

    public GroupPosts() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getShared_userid() {
        return shared_userid;
    }

    public void setShared_userid(String shared_userid) {
        this.shared_userid = shared_userid;
    }

    public String getShared_postid() {
        return shared_postid;
    }

    public void setShared_postid(String shared_postid) {
        this.shared_postid = shared_postid;
    }

    public boolean isKen_burns_effect() {
        return ken_burns_effect;
    }

    public void setKen_burns_effect(boolean ken_burns_effect) {
        this.ken_burns_effect = ken_burns_effect;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public boolean isDownloadEnabled() {
        return downloadEnabled;
    }

    public void setDownloadEnabled(boolean downloadEnabled) {
        this.downloadEnabled = downloadEnabled;
    }

    public boolean isScreenshotEnabled() {
        return screenshotEnabled;
    }

    public void setScreenshotEnabled(boolean screenshotEnabled) {
        this.screenshotEnabled = screenshotEnabled;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
