package com.example.deimos_events;

import java.util.Date;

/**
 * Class for notifications and what they include
 */
public class Notifications {
    String id;
    private String eventId;
    public String message;
    public String senderId;
    public String recipientId;
    public String registrationId;
    public String title;
    public String image;
    
    public String status;
    public Date time;
    
    public Notifications() {}
    
    public Notifications(String id, String eventId, String message, String senderId, String recipientId, String registrationId) {
        this.id = id;
        this.eventId = eventId;
        this.message = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.registrationId = registrationId;
        this.time = new Date();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getRegistrationId() {
        return registrationId;
    }
    
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
    
    public void setTitle(String title) { this.title = title; }
    public void setImage(String image) { this.image = image; }
    
    public String getTitle() { return title; }
    public String getImage() { return image; }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getTime() {
        return time;
    }
    
    public void setTime(Date time) {
        this.time = time;
    }
}
