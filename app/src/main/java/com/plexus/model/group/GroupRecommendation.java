package com.plexus.model.group;

public class GroupRecommendation {

    private int id;
    private String groupId;

    public GroupRecommendation(int id, String groupId){
        this.id = id;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
