package com.example.deimos_events;

import android.graphics.Bitmap;

import com.google.zxing.common.BitMatrix;

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
    String registrationDeadline;
    Integer participantCap;
    Boolean recordLocation;
    String qrCodeId;

    public Event(String id, String title, String posterId, String description, String registrationDeadline, Integer participantCap, Boolean recordLocation, String qrCodeId) {
        this.id = id;
        this.title = title;
        this.posterId = posterId;
        this.description = description;
        this.registrationDeadline = registrationDeadline;
        this.participantCap = participantCap;
        this.recordLocation = recordLocation;
        this.qrCodeId = qrCodeId;
    }
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
}


