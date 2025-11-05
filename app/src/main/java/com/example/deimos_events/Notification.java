package com.example.deimos_events;

/**
 * Notification class. These get created assuming that the intended recipient has not opted
 * out of notifications.
 */
public class Notification {
    String id;
    String receivingActorId;
    String sendingActorId;
    String message;

    public Notification(String id, String receivingActorId, String sendingActorId, String message) {
        this.id = id;
        this.receivingActorId = receivingActorId;
        this.sendingActorId = sendingActorId;
        this.message = message;
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

    public String getSendingActorId() {
        return sendingActorId;
    }

    public void setSendingActorId(String sendingActorId) {
        this.sendingActorId = sendingActorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
