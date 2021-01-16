package com.plexus.model.group;

public class Group {

    private String id;
    private String createdBy;
    private String name;
    private String about;
    private String coverImageUrl;
    private String type;
    private String location;
    private boolean visible;
    private String createdAt;
    private String colour;

    public Group(String id, String createdBy, String name, String about, String coverImageUrl, String type, String location, boolean visible, String createdAt, String colour) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.about = about;
        this.coverImageUrl = coverImageUrl;
        this.type = type;
        this.location = location;
        this.visible = visible;
        this.createdAt = createdAt;
        this.colour = colour;
    }

    public Group() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
