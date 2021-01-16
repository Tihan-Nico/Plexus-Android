package com.plexus.model.group;

public class GroupSettings {

    private String privacy;
    private String postPrivacy;
    private boolean postApproval;

    public GroupSettings(String privacy, String postPrivacy, boolean postApproval) {
        this.privacy = privacy;
        this.postPrivacy = postPrivacy;
        this.postApproval = postApproval;
    }

    public GroupSettings() {

    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPostPrivacy() {
        return postPrivacy;
    }

    public void setPostPrivacy(String postPrivacy) {
        this.postPrivacy = postPrivacy;
    }

    public boolean isPostApproval() {
        return postApproval;
    }

    public void setPostApproval(boolean postApproval) {
        this.postApproval = postApproval;
    }
}
