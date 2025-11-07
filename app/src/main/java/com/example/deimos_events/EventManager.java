package com.example.deimos_events;
import android.graphics.Bitmap;
import android.util.Base64;

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
        // should be eventExists
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

    /**
     *  This method attempts to delete a {@link Registration} object from the database
     *  <p>
     *  This method first checks certain conditions before querying the database to delete
     *
     *  <p>
     *  Checks that the Registration object is not null
     *  <p>
     *  Checks if the Registration actually exists in the database using
     *  {@code Database.registrationExists()}
     *  <p>
     *  This operation talks to the database so it is asynchronous
     *
     * @param register the registration object we seek to delete
     * @param callback a Consumer that takes in a {@link Result} which is the outcome
     *                 of the delete attempt. Callback is made even if the database isn't queried
     *
     *
     * @see Session
     * @see Registration
     * @see Database#deleteRegistor(String, Consumer)
     * @see Database#registrationExists(String, Consumer)
     */
    public void deleteRegistration(Registration register, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (register == null){
            callback.accept(new Result(Boolean.FALSE, "DELETE_REGISTRATION", "No registration given"));
            return;
        }

        db.registrationExists(register.getId(), exists ->{
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "DELETE_REGISTRATION", "Database failed to read"));
            } else if (exists){
                db.deleteRegistor(register.getId(), delresult ->{
                    if (delresult){
                        callback.accept(new Result(Boolean.TRUE, "DELETE_REGISTRATION", "Success, registration deleted"));
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "DELETE_REGISTRATION", "Failed to delete registration"));
                    }
                });
            } else {
                callback.accept(new Result(Boolean.FALSE, "DELETE_REGISTRATION", "Registration does not exist in database"));
            }
        });
    }
    /**
     * This method tries to fetch a singular {@link Event} object from the database and then stores it
     * into the current {@link Session} object.
     * <p>
     * If the event isn't inside of the database, the session object is not mutated
     * <p>
     *  This operation talks to the database so it is asynchronous
     *  <ul>
     *      <li>{@code cond} is set to true if the fetch succeeded</li>
     *      <li>{@code cond} is set to false if fetch failed or the registration was not found</li>
     *      <li>{@code type = "FETCH_EVENT"}</li>
     *  </ul>
     *
     *
     * This method talks to the database and therefore is async
     * </p>
     * @param eventId the unique identifier for the event
     * @param callback takes in the @{link Result} object which tells us the result of the query
     */
    public void fetchEventById(String eventId, Consumer<Result> callback){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchEventById(eventId, event ->{
            if (event != null){
                session.setCurrentEvent(event);
                callback.accept(new Result(Boolean.TRUE, "FETCH_EVENT", "Fetched event successfully"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "FETCH_EVENT", "Fetched Failed"));
            }
        });
    }

    /**
     * Fetches all {@link Registration} objects that are connected to the eventId
     * this method is asynchronous; the return value is given via the callback
     * @param eventId
     * @param callback takes in a list of registration objects or null if the query fails
     */
    public void fetchAllRegistrations(String eventId, Consumer<List<Registration>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchALLRegistrations(eventId, regList ->{
            if (regList != null){
                List<Registration> registrations = new ArrayList<>();
                for (Registration regSlice : regList){
                    registrations.add(regSlice);
                }
                callback.accept(registrations);
            } else {
                callback.accept(null);
            }
        });


    }

    /**
     * Gets the number of pending (waiting listed) registrations for the event determined by the eventId
     * @param eventID the id of the event we are querying
     * @param callback gets the number of pending registrations, else {@code null} on failure
     */
    public void getWaitingListCount(String eventID, Consumer<Integer> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getPendingRegistrationsForEvent(eventID, callback);
    }


    /**
     *  WIll try to place the current {@link Actor} into the waiting list for given event
     *  <p>
     *  This operation queries the database so it is asynchronous
     *
     * @param eventId the ID of the event we are trying to join
     * @param callback is set to true if we are successful in adding the user to the waiting liest,
     *                 else false
     */
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
//
//    // below are not used, will maybe implemented
//    public void getEventById(String eventId, Consumer<Event> callback) {
//        Session session = sessionManager.getSession();
//        IDatabase db = session.getDatabase();
//        DocumentReference docref = db.getEvent(eventId, success -> {
//            Result r;
//            if (success) {
//                r = new Result(Boolean.TRUE, "creating event", "Succeeded on creating Event");
//            } else {
//                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
//            }
//        });
//        docref.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                callback.accept(documentSnapshot.toObject(Event.class));
//            }
//
//        });
//
//    }
//    public void getImage(String eventId, Consumer<Event> callback) {
//        Bitmap image;
//        Session session = sessionManager.getSession();
//        IDatabase db = session.getDatabase();
//        DocumentReference docref = db.getEvent(eventId, event -> {
//            Result r;
//            if (event) {
//                r = new Result(Boolean.TRUE, "creating event", "Succeeded on creating Event");
//            } else {
//                r = new Result(Boolean.FALSE, "creating event", "Failed to create Event");
//            }
//        });
//        Event event;
//        docref.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                callback.accept(documentSnapshot.toObject(Event.class));
//            }
//        });
//
//    }
//    public void updateImage(String eventId, Bitmap imageBit) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        imageBit.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String posterIdArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        Session session = sessionManager.getSession();
//        IDatabase db = session.getDatabase();
//        db.updateImage(eventId, posterIdArray);
//    }

}
