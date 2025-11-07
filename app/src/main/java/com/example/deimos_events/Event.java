package com.example.deimos_events;

import java.util.Date;
import java.util.List;

/**
 * A class for events. This also contains methods for getting data related to registrations and users.
 */
public class Event {
    String id;
    String title;
    String posterId;
    String description;
    Date registrationDeadline;
    Number participantCap;
    Boolean recordLocation;
    String qrCodeId;

    String guidelines;
    String criteria;
    String time;
    String location;
    Date date;





    public Event(String id, String title, String posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation) {
        this.id = id;
        this.title = title;
        this.posterId = posterId;
        this.description = description;
        this.registrationDeadline = registrationDeadline;
        this.participantCap = participantCap;
        this.recordLocation = recordLocation;
        this.qrCodeId = qrCodeId;
        this.criteria = criteria;
        this.guidelines = guidelines;
    }
    public Event(){}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Number getParticipantCap() {
        return participantCap;
    }

    public void setParticipantCap(Number participantCap) {
        this.participantCap = participantCap;
    }

    public Boolean getRecordLocation() {
        return recordLocation;
    }

    public void setRecordLocation(Boolean recordLocation) {
        this.recordLocation = recordLocation;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuidelines(){return guidelines;}
    public String getCriteria(){return criteria;}

    public Date getDate() {
        return date;
    }
    public String getLocation() {
        return location;
    }
    public String getTime() {
        return time;
    }
}


