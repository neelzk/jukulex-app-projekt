package com.jukulex.juz;

public class UserProperties {
    private boolean postEventsAllowed;
    private boolean postMapMarkersAllowed;
    private boolean commentAllowed;

    public UserProperties() {
        this.postEventsAllowed = false;
        this.postMapMarkersAllowed = true;
        this.commentAllowed = true;
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

}
