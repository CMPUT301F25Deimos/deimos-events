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
    Bitmap posterId;
    String description;
    Date registrationDeadline;
    Number participantCap;
    Boolean recordLocation;
    BitMatrix qrCodeId;

    public Event(String id, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
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

    public Bitmap getPosterId() {
        return posterId;
    }

    public void setPosterId(Bitmap posterId) {
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

    public BitMatrix getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(BitMatrix qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;

    }


}


