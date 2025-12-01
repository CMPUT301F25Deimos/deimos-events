package com.example.deimos_events.dataclasses;

/**
 * Represents a registration of an entrant into an event.
 * A registration tracks the entrant's status, which may be Declined, Waitlisted,
 * Pending, Accepted, Message, or Waiting. Pending indicates unanswered, Waitlisted
 * means not chosen, Waiting means the lottery has not been applied, and Message
 * indicates the sender is only sending information. Images are taken from the event,
 * and titles/descriptions correspond to notification messages.
 */
public class Registration {
    /** Recorded longitude of the entrant at registration time. */
    public String longitude;
    /** Recorded latitude of the entrant at registration time. */
    public String latitude;
    /** Unique identifier of the registration. */
    private String id;
    /** The ID of the entrant associated with this registration. */
    private String entrantId;
    /** The ID of the event associated with this registration. */
    private String eventId;
    /** The status of the registration. */
    private String status;
    /** Optional title associated with the registration. */
    public String title;
    /** Optional image associated with the registration. */
    public String image;

    /**
     * Default constructor for serialization.
     */
    public Registration() {}

    /**
     * Creates a new registration record.
     * @param id the registration ID
     * @param entrantId the ID of the entrant
     * @param eventId the ID of the event
     * @param status the status of the registration
     * @param latitude the recorded latitude
     * @param longitude the recorded longitude
     */
    public Registration(String id, String entrantId, String eventId, String status, String latitude, String longitude) {
        this.id = id;
        this.entrantId = entrantId;
        this.eventId = eventId;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return the registration ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the registration ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the entrant ID
     */
    public String getEntrantId() {
        return entrantId;
    }

    /**
     * @param entrantId the entrant ID to set
     */
    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    /**
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @param eventId the event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the registration status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the registration status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /** Sets the title of the registration. */
    public void setTitle(String title) { this.title = title; }

    /** Sets the image associated with the registration. */
    public void setImage(String image) { this.image = image; }

    /**
     * @return the longitude value
     */
    public String getLongitude() { return this.longitude; }

    /**
     * @return the latitude value
     */
    public String getLatitude() { return this.latitude; }
}
