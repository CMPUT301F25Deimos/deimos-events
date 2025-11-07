package com.example.deimos_events;

/**
 * A class for events. This also contains methods for getting data related to registrations and users.
 */
public class Event {
    String id;
    String title;
    String posterId;
    String description;
    String registrationDeadline;
    Integer participantCap;
    Boolean recordLocation;
    String qrCodeId;
    String guidelines;
    String criteria;
    String time;
    String location;
    String date;
    String ownerId;

    public Event(String id, String title, String posterId, String description, String registrationDeadline, Integer participantCap, Boolean recordLocation, String qrCodeId, String ownerId) {
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
        this.ownerId = ownerId;
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

    public String getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(String registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Number getParticipantCap() {
        return participantCap;
    }

    public void setParticipantCap(Integer participantCap) {
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

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public String getGuidelines(){return guidelines;}
    public String getCriteria(){return criteria;}

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }
    public String getLocation() {
        return location;
    }
    public String getTime() {
        return time;
    }
    
    public String getOwnerId() { return ownerId; }
}


