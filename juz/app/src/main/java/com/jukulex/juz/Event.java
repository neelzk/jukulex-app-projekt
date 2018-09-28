package com.jukulex.juz;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.List;

public class Event {
    private Date startDate;
    private Date endDate;
    private String description;
    private String title;
    private List<String> participants;
    private GeoPoint location;

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public String getTitle() { return title; }

    public List<String> getParticipants() { return participants; }

    public GeoPoint getLocation() { return location; }

    public String toString() {
        StringBuilder sb = new StringBuilder("Veranstaltung: ");

        sb.append(title);

        if (description != null)
            sb.append(" (").append(description).append(")");

        if (startDate != null)
            sb.append(" Start: ").append(startDate.toString());

        if (endDate != null)
            sb.append(" Ende: ").append(endDate.toString());

        if (participants != null)
            sb.append(' ').append(participants.size()).append(" Teilnehmer.");

        if (location != null)
            sb.append(" Ort: ").append(location.getLatitude()).append(", ").append(location.getLongitude());

        return sb.toString();
    }
}
