package com.example.deimos_events.dataclasses;

import java.util.Date;

/**
 * Represents a notification within the app, including information about
 * the message, sender, recipient, event association, and timestamp.
 * Notifications include a time field so they can be sorted when displayed.
 */
public class Notifications {

    /** Unique identifier for the notification. */
    String id;

    /** Identifier of the event associated with this notification. */
    private String eventId;

    /** Text content of the notification message. */
    public String message;

    /** The ID of the actor who sent the notification. */
    public String senderId;

    /** The ID of the actor receiving the notification. */
    public String recipientId;

    /** The registration ID associated with this notification, if any. */
    public String registrationId;

    /** Optional title for the notification. */
    public String title;

    /** Optional image reference for the notification. */
    public String image;

    /** Status of the notification (e.g., read, unread). */
    public String status;

    /** Timestamp representing when the notification was created. */
    public Date time;

    /**
     * Default constructor used for serialization.
     */
    public Notifications() {}

    /**
     * Constructs a new notification with the specified parameters.
     * Automatically sets the timestamp to the current time.
     *
     * @param id the unique identifier for the notification
     * @param eventId the ID of the associated event
     * @param message the message body of the notification
     * @param senderId the ID of the actor sending the notification
     * @param recipientId the ID of the actor receiving the notification
     * @param registrationId the associated registration ID, if any
     */
    public Notifications(String id, String eventId, String message, String senderId, String recipientId, String registrationId) {
        this.id = id;
        this.eventId = eventId;
        this.message = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.registrationId = registrationId;
        this.time = new Date();
    }

    /**
     * Returns the unique ID of the notification.
     *
     * @return the notification ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique ID of the notification.
     *
     * @param id the notification ID to assign
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the ID of the event associated with the notification.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with the notification.
     *
     * @param eventId the event ID to assign
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the message content of the notification.
     *
     * @return the message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content of the notification.
     *
     * @param message the message text to assign
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the ID of the sender.
     *
     * @return the sender ID
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the ID of the sender.
     *
     * @param senderId the sender ID to assign
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the ID of the recipient.
     *
     * @return the recipient ID
     */
    public String getRecipientId() {
        return recipientId;
    }

    /**
     * Sets the ID of the recipient.
     *
     * @param recipientId the recipient ID to assign
     */
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Returns the associated registration ID.
     *
     * @return the registration ID
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * Sets the associated registration ID.
     *
     * @param registrationId the registration ID to assign
     */
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    /** Sets the title of the notification. */
    public void setTitle(String title) { this.title = title; }

    /** Sets the image reference for the notification. */
    public void setImage(String image) { this.image = image; }

    /** Returns the title of the notification. */
    public String getTitle() { return title; }

    /** Returns the image reference of the notification. */
    public String getImage() { return image; }

    /**
     * Returns the status of the notification.
     *
     * @return the notification status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the notification.
     *
     * @param status the status to assign
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the timestamp when the notification was created.
     *
     * @return the creation time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the timestamp for the notification.
     *
     * @param time the timestamp to assign
     */
    public void setTime(Date time) {
        this.time = time;
    }
}
