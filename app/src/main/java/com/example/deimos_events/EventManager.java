package com.example.deimos_events;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Handles persistent operations connected to the {@link Event} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Event} objects temporarily, but all persistent operations
 * must be performed via the {@code EventManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class EventManager {
    private final SessionManager sessionManager;

    public EventManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    public void createEvent(String id, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
        Event event = new Event(id,title,posterId,description,registrationDeadline,participantCap,recordLocation,qrCodeId);
        Session session = sessionManager.getSession();
        Database db = (Database) session.getDatabase();
        db.createEvent(event, success ->{
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE,"creating event", "Succeeded on creating Event");
            } else {
                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
            }
        });
    }

    public void getEventById(String eventId, Consumer<Event> callback) {
        Session session = sessionManager.getSession();
        Database db = (Database) session.getDatabase();
        DocumentReference docref = db.getEvent(eventId , success ->{
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE,"creating event", "Succeeded on creating Event");
            } else {
                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
            }
        });

        docref.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                Event event =  documentSnapshot.toObject(Event.class);
                callback.accept(documentSnapshot.toObject(Event.class));
            }

        });

        Task<DocumentSnapshot> snapshot = docref.get();
        return;
    }
}
