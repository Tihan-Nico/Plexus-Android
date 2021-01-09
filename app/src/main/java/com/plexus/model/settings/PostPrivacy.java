package com.plexus.model.settings;

public class PostPrivacy {

    private boolean allow_download;
    private boolean allow_screenshot;

    public PostPrivacy(boolean allow_download, boolean allow_screenshot){
        this.allow_download = allow_download;
        this.allow_screenshot = allow_screenshot;
    }

    public PostPrivacy(){

    }

    public boolean isAllow_download() {
        return allow_download;
    }

    public void setAllow_download(boolean allow_download) {
        this.allow_download = allow_download;
    }

    public boolean isAllow_screenshot() {
        return allow_screenshot;
    }

    public void setAllow_screenshot(boolean allow_screenshot) {
        this.allow_screenshot = allow_screenshot;
    }
}
