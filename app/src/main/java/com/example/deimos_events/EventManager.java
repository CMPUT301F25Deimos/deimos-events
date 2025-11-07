package com.example.deimos_events;
import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.firestore.DocumentReference;
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

    public EventManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }


    /**
     * Makes a {@link Event} by encoding a poster {@link Bitmap} and a QR {@link BitMatrix}
     * into a form that can be stored in the Database, specifically Base64 strings
     * @param id
     * @param title
     * @param posterId
     * @param description
     * @param registrationDeadline
     * @param participantCap
     * @param recordLocation
     * @param qrCodeId
     * @return  Brand new {@link Event} Object
     */
    public Event createEvent(String id, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId ){
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
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] qrBytes = out.toByteArray();
        String qrArray = Base64.encodeToString(qrBytes, Base64.DEFAULT);
        return new Event(id, title, posterIdArray, description, registrationDeadline.toString(), participantCap.intValue(),recordLocation, qrArray);
    }


    /**
     * This method will attempt to insert the current {@link Event} into the Database.
     * <p>
     * This method does some validation before issuing the insertion request to the database
     * <p>
     * Verifies that the current event is not present in the Database.
     * <p>
     * If the event is not present in the database it will send an insert request via
     * {@code Database.insertEvent()}
     * <p>
     *
     * On Success will add the event to the {@link Session}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the insertion succeeded</li>
     *     <li>{@code cond = false} if the operation failed or the event was already found in the database
     *     <li>{@code type = "INSERT_EVENT"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param callback a {@link Consumer} that receives a {@link Result} which represents the
     *                 outcome of the insertion attempt. The callback is always invoked even if the
     *                 database is not queried.
     *
     * @see Session
     * @see Event
     * @see Database#insertEvent(Event, Consumer)
     */
    public void insertEvent(Event event, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // grab session, database, and what you need, in this case the actor

        // Validate what you are trying to do, before querying the database
        if (event == null){
            callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "No EVENT found"));
            return;
        }
        // Validate the query
        db.insertEvent(event, exists ->{
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Database Failed to Read"));
            }else if (exists){
                callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Event already exists"));
            }else{
                db.insertEvent(event, createResult -> {
                    if (createResult) {
                        callback.accept(new Result(Boolean.TRUE, "INSERT_EVENT",  "Successfully created event"));
                        session.setCurrentEvent(event); // add event to session.
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Failed to write event"));
                    }
                });
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
        Bitmap image;
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        DocumentReference docref = db.getEvent(eventId, event -> {
                    Result r;
                    if (event) {
                        r = new Result(Boolean.TRUE, "creating event", "Succeeded on creating Event");
                    } else {
                        r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
                    }
                });
                Event event;
                docref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    callback.accept(documentSnapshot.toObject(Event.class));
                }
            });

    }


    public void deleteRegistration(Registration register, Consumer<Boolean> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.deleteRegistor(register.getEntrantId(), register.getEventId());
    }

    public void updateImage(String eventId, Bitmap imageBit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String posterIdArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.updateImage(eventId, posterIdArray);
    }


    public List<Registration> getRegistration(String eventId) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        List<Registration> registrations = new ArrayList<>();
        db.getRegistration(eventId, callback -> {
            if(callback!=null) {
                for (Registration document : callback) {
                    Registration registration = document;
                    registrations.add(registration);
                }
            }
        });
        return registrations;
    }
    public void getWaitingListCount(String eventID, Consumer<Integer> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getPendingRegistrationsForEvent(eventID, count->{
            callback.accept(count);
        });
    }
    public void addUserToWaitList(String eventId, Consumer<Boolean> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        if (actor == null){
            callback.accept(false);
            return;
        }
        db.addUserToWaitList(eventId, actor, callback);
    }

    public void fetchEventById(String eventId, Consumer<Event> callback){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getEventById(eventId, callback);
    }

}
