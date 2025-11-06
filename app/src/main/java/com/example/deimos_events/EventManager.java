package com.example.deimos_events;

/**
 * Handles persistent operations connected to the {@link Event} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Event} objects temporarily, but all persistent operations
 * must be performed via the {@code EventManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public EventManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void createEvent(String id, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        posterId.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String posterIdArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        int width = qrCodeId.getWidth();
        int height = qrCodeId.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, qrCodeId.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] qrbytes = baos.toByteArray();
        String qrArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Event event = new Event(id, title, posterIdArray, description, registrationDeadline, participantCap, recordLocation, qrArray);
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        db.createEvent(event, success -> {
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE, "creating event", "Succeeded on creating Event");
            } else {
                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
            }
        });
    }

    public void getEventById(String eventId, Consumer<Event> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        DocumentReference docref = db.getEvent(eventId, success -> {
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE, "creating event", "Succeeded on creating Event");
            } else {
                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
            }
        });

        docref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.accept(documentSnapshot.toObject(Event.class));
            }

        });


    }
    public void getImage(String eventId, Consumer<Event> callback) {
        getEventById(eventId, event -> {
            if (event != null) {
                callback.accept(event);
            } else {
                Log.e("Database", "No event found with ID: " + eventId);
                callback.accept(null);
            }
            sessionManager.setResult(r);
        });
    }



        public void deleteRegistor(Register register, Consumer<Boolean> callback) {
            Session session = sessionManager.getSession();
            Database db = session.getDatabase();
            db.deleteRegistor(register.getEventId,register.getActorId);
        }
    public void updateImage(String eventId, Bitmap imageBit)   {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String posterIdArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        db.updateImage(eventId,posterIdArray);
    }
