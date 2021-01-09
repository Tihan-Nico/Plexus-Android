package com.plexus.model.group;

public class GroupRules {

    private int id;
    private String name;
    private String description;

    public GroupRules(int id, String name, String description){
        this.description = description;
        this.name = name;
        this.description = description;
    }

    public GroupRules(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
