package com.jukulex.juz;

public class UserProperties {
    private boolean postEventsAllowed;
    private boolean postMapMarkersAllowed;
    private boolean commentAllowed;
    private boolean deactivated;

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
