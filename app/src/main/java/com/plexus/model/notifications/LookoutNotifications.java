package com.plexus.model.notifications;

public class LookoutNotifications {

    String id;
    String profileImage;
    String profileName;
    String profileID;
    String postID;
    String timestamp;
    String type;
    boolean notificationViewed;
    boolean notificationRead;

    public LookoutNotifications(String id, String profileImage, String profileName, String profileID, String postID, String timestamp, String type, boolean notificationRead, boolean notificationViewed) {
        this.id = id;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.profileID = profileID;
        this.postID = postID;
        this.timestamp = timestamp;
        this.type = type;
        this.notificationRead = notificationRead;
        this.notificationViewed = notificationViewed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

}
