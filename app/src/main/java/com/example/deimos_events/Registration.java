package com.example.deimos_events;

/**
 * Registration class. This represents the registration of an entrant into an event. The status
 * can either be Declined, Not Selected, Pending, or Accepted.
 * Pending is the equivalent of being in a waiting list for an event.
 */
public class Registration {
    String id;
    String entrantId;
    String eventId;
    String status;

    public Registration(String id, String entrantId, String eventId, String status) {
        this.id = id;
        this.entrantId = entrantId;
        this.eventId = eventId;
        this.status = status;
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
}
