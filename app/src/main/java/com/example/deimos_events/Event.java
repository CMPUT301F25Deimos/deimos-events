package com.example.deimos_events;

import android.graphics.Bitmap;

import com.google.zxing.common.BitMatrix;

import java.util.Date;
import java.util.List;

/**
 * A class for events
 */
public class Event {
    String id;
    List<String>  waitingListParticipantIds;
    String title;
    Bitmap posterId;
    String description;
    Date registrationDeadline;
    Number participantCap;
    Boolean recordLocation;
    BitMatrix qrCodeId;

    public Event(String id, List<String> waitingListParticipantIds, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
        this.id = id;
        this.waitingListParticipantIds = waitingListParticipantIds;
        this.title = title;
        this.posterId = posterId;
        this.description = description;
        this.registrationDeadline = registrationDeadline;
        this.participantCap = participantCap;
        this.recordLocation = recordLocation;
        this.qrCodeId = qrCodeId;
    }
    public String getId(){return id;}
    public Number getNumberOfEntrants() {
        return waitingListParticipantIds.size();
    }
}
