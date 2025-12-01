package com.example.deimos_events.dataclasses;
/**
 * Represents an invitation sent from an organizer to an entrant.
 * <p>
 * An invitation corresponds to an application for an event. If the invitation
 * is rejected, the related registration will be marked as "Declined". The
 * invitation record will persist in the system and is never deleted.
 * </p>
 */
public class Invitation {
    /** Unique identifier for the invitation. */
    String id;
    /** The ID of the actor receiving the invitation. */
    String receivingActorId;
    /** The ID of the event for which the invitation is sent. */
    String eventId;
    /**
     * Constructs a new Invitation object with the provided details.
     *
     * @param id the unique identifier for the invitation
     * @param receivingActorId the ID of the actor receiving the invitation
     * @param eventId the ID of the event associated with the invitation
     */
    public Invitation(String id, String receivingActorId, String eventId) {
        this.id = id;
        this.receivingActorId = receivingActorId;
        this.eventId = eventId;
    }
    /**
     * @return the unique identifier of the invitation
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the unique identifier to assign to this invitation
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the ID of the actor who received the invitation
     */
    public String getReceivingActorId() {
        return receivingActorId;
    }
    /**
     * @param receivingActorId the ID of the recipient actor to set
     */
    public void setReceivingActorId(String receivingActorId) {
        this.receivingActorId = receivingActorId;
    }
    /**
     * @return the ID of the event associated with this invitation
     */
    public String getEventId() {
        return eventId;
    }
    /**
     * @param eventId the ID of the event to associate with this invitation
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
