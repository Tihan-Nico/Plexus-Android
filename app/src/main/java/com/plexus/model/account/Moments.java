package com.plexus.model.account;

public class Moments {

    private String name;
    private String image_url;

    private Moments(String name, String image_url){
        this.name = name;
        this.image_url = image_url;
    }

    private Moments(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
