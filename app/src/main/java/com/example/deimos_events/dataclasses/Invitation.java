package com.example.deimos_events.dataclasses;

/**
 * Invitation class. This simply represents an invite sent from an organizer to an applicant.
 * If rejected, then the corresponding registration will be marked as Declined.
 * This record will persist and not get deleted.
 */
public class Invitation {
    String id;
    String receivingActorId;
    String eventId;

    public Invitation(String id, String receivingActorId, String eventId) {
        this.id = id;
        this.receivingActorId = receivingActorId;
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceivingActorId() {
        return receivingActorId;
    }

    public void setReceivingActorId(String receivingActorId) {
        this.receivingActorId = receivingActorId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
