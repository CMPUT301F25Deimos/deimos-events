package com.example.deimos_events;

import android.graphics.Bitmap;

/**
 * Registration class. This represents the registration of an entrant into an event. The status
 * can either be Declined, Not Selected, Pending, or Accepted.
 * Pending is the equivalent of being in a waiting list for an event.
 * Image will be taken from the events image, and description will be the notification message containing the title of the
 * event, saying whether the user was accepted or not
 */
public class Registration {
    public String longitude;
    public String latitude ;
    private String id;
    private String entrantId;
    private String eventId;
    private String status;
    
    public String description;
    public String image;
    
    public Registration() {}

    public Registration(String id, String entrantId, String eventId, String status, String latitude ,String longitude) {
        this.id = id;
        this.entrantId = entrantId;
        this.eventId = eventId;
        this.status = status;
        this.latitude  = latitude ;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setDescription(String description) { this.description = description; }
    public void setImage(String image) { this.image = image; }

    public String getLongitude() {return this.longitude;
    }
    public String getLatitude () {return this.latitude;}
}
