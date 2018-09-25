package com.jukulex.juz;

public class UserProperties {
    private boolean postEventsAllowed;
    private boolean postMapMarkersAllowed;
    private boolean commentAllowed;
    private boolean deactivated;

    public UserProperties() {
        this.postEventsAllowed = false;
        this.postMapMarkersAllowed = true;
        this.commentAllowed = true;
        this.deactivated = false;
    }

    public boolean isPostEventsAllowed() {
        return postEventsAllowed;
    }

    public boolean isPostMapMarkersAllowed() {
        return postMapMarkersAllowed;
    }

    public boolean isCommentAllowed() {
        return commentAllowed;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

}
