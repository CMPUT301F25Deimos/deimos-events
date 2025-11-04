package com.example.deimos_events;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;

import com.google.firebase.firestore.DocumentReference;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    private final SessionManager sessionManager;

    public EventManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    public void createEvent(String id, ArrayList<String> waitingListParticipantIds, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
        Event event = new Event(id,waitingListParticipantIds,title,posterId,description,registrationDeadline,participantCap,recordLocation,qrCodeId);
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        db.createEvent(event, success ->{
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE, "Succeeded on creating Event");
            } else {
                r = new Result(Boolean.FALSE, "Failed to create Event");
            }
            sessionManager.setResult(r);
        });
    }
}
