package com.plexus.model.posts;

public class Hashtag {

    private String createdBy;
    private String createdAt;

    public Hashtag(String createdBy, String createdAt) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Hashtag() {

    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
