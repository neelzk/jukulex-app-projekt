package com.jukulex.juz;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class MapMarker {
    private LatLng loc;
    private GeoPoint location;
    private String title;
    private String snippet;
    private String userId;

    public MapMarker() { }

    public MapMarker(GeoPoint location, String title, String snippet, String userId) {
        this.location = location;
        this.title = title;
        this.snippet = snippet;
        this.userId = userId;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getUserId() {
        return userId;
    }
}
