package com.example.deimos_events.ui.events;

import java.util.Date;

/**
 * A class for events
 */
public class Event {
    String id;
    String[] waitingListParticipantIds;
    String title;
    String posterId;
    String description;
    Date registrationDeadline;
    Number participantCap;
    Boolean recordLocation;
    String qrCodeId;
    Boolean notification;
    
    

    public Event(String id, String[] waitingListParticipantIds, String title, String posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, Boolean notification) {
        this.id = id;
        this.waitingListParticipantIds = waitingListParticipantIds;
        this.title = title;
        this.posterId = posterId;
        this.description = description;
        this.registrationDeadline = registrationDeadline;
        this.participantCap = participantCap;
        this.recordLocation = recordLocation;
        this.qrCodeId = qrCodeId;
        this.notification = notification;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public Number getNumberOfEntrants() {
        return waitingListParticipantIds.length;
    }
}
