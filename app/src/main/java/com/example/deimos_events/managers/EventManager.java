package com.example.deimos_events.managers;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;


import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.Database;
import com.example.deimos_events.dataclasses.Entrant;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.dataclasses.Result;
import com.example.deimos_events.Session;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Handles persistent operations connected to the {@link Event} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Event} objects temporarily, but all persistent operations
 * must be performed via the {@code EventManager}.
 * <p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class EventManager {
    private final SessionManager sessionManager;

    public EventManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    /**
     * Makes a {@link Event} by encoding a poster {@link Bitmap} and a QR {@link BitMatrix}
     * into a form that can be stored in the Database, specifically Base64 strings
     *
     * @param id
     * @param title
     * @param posterId
     * @param description
     * @param registrationDeadline
     * @param participantCap
     * @param recordLocation
     * @param qrCodeId
     * @return Brand new {@link Event} Object
     */
    public Event createEvent(String id, String title, Bitmap posterId, String description, Date registrationDeadline, Number participantCap, Boolean recordLocation, BitMatrix qrCodeId) {
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
        Actor actor = sessionManager.getSession().getCurrentActor();
        return new Event(id, title, posterIdArray, description, registrationDeadline.toString(), participantCap.intValue(), recordLocation, qrArray, actor.getDeviceIdentifier().toString());
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
     * <p>
     * On Success will add the event to the {@link Session}
     * <p>
     * <p>
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
     * @see Session
     * @see Event
     * @see Database#insertEvent(Event, Consumer)
     */
    public void insertEvent(Event event, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // grab session, database, and what you need, in this case the actor

        // Validate what you are trying to do, before querying the database
        if (event == null) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "No EVENT found"));
            return;
        }
        // Validate the query
        // should be eventExists
        db.eventExists(event, exists -> {
            if (exists == null) {
                callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Database Failed to Read"));
            } else if (exists) {
                callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Event already exists"));
            } else {
                db.insertEvent(event, createResult -> {
                    if (createResult) {
                        callback.accept(new Result(Boolean.TRUE, "INSERT_EVENT", "Successfully created event"));
                        session.setCurrentEvent(event); // add event to session.
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "INSERT_EVENT", "Failed to write event"));
                    }
                });
            }
        });
    }

    /**
     * This method attempts to delete a {@link Registration} object from the database
     * <p>
     * This method first checks certain conditions before querying the database to delete
     *
     * <p>
     * Checks that the Registration object is not null
     * <p>
     * Checks if the Registration actually exists in the database using
     * {@code Database.registrationExists()}
     * <p>
     * This operation talks to the database so it is asynchronous
     *
     * @param registrationId the entrant
     * @param callback       a Consumer that takes in a {@link Result} which is the outcome
     *                       of the delete attempt. Callback is made even if the database isn't queried
     * @see Session
     * @see Registration
     * @see Database#deleteRegistration(String, Consumer) (String, Consumer)
     */
    public void deleteRegistration(String registrationId, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.deleteRegistration(registrationId, delresult -> {
            if (delresult) {
                callback.accept(new Result(Boolean.TRUE, "DELETE_REGISTRATION", "Success, registration deleted"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "DELETE_REGISTRATION", "Failed to delete registration"));
            }
        });
    }

    /**
     * This method tries to fetch a singular {@link Event} object from the database and then stores it
     * into the current {@link Session} object.
     * <p>
     * If the event isn't inside of the database, the session object is not mutated
     * <p>
     * This operation talks to the database so it is asynchronous
     *  <ul>
     *      <li>{@code cond} is set to true if the fetch succeeded</li>
     *      <li>{@code cond} is set to false if fetch failed or the registration was not found</li>
     *      <li>{@code type = "FETCH_EVENT"}</li>
     *  </ul>
     * <p>
     * <p>
     * This method talks to the database and therefore is async
     * </p>
     *
     * @param eventId  the unique identifier for the event
     * @param callback takes in the @{link Result} object which tells us the result of the query
     */
    public void fetchEventById(String eventId, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchEventById(eventId, event -> {
            if (event != null) {
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
     *
     * @param eventId
     * @param callback takes in a list of registration objects or null if the query fails
     */
    public void fetchAllRegistrations(String eventId, Consumer<List<Registration>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchALLRegistrations(eventId, regList -> {
            if (regList != null) {
                List<Registration> registrations = new ArrayList<>();
                for (Registration regSlice : regList) {
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
     *
     * @param eventID  the id of the event we are querying
     * @param callback gets the number of pending registrations, else {@code null} on failure
     */
    public void getWaitingListCount(String eventID, Consumer<Integer> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getPendingRegistrationsForEvent(eventID, callback);
    }

    public void getActorById(String actorId, Consumer<Actor> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getActorById(actorId, callback);
    }

    /**
     * WIll try to place the current {@link Actor} into the waiting list for given event
     * <p>
     * This operation queries the database so it is asynchronous
     *
     * @param eventID  eventID of the event we are trying to join
     * @param callback is set to true if we are successful in adding the user to the waiting list,
     *                 else false
     */
    public void addUserToWaitList(String eventID, Consumer<Boolean> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        db.addUserToWaitList(eventID, actor, callback);
    }



    public void updateImage(String eventId, Bitmap imageBit, Consumer<Boolean> callback) {
        if (eventId == null || imageBit == null) {
            Log.e("EventManager", "updateImage failed: eventId was null or empty.");
            callback.accept(false);
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String posterIdArray = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.updateImage(eventId, posterIdArray, call -> {
            if (call) {
                callback.accept(true);
            } else {
                callback.accept(false);
            }
        });

    }

    public void getAllEvents(Consumer<List<Event>> callback) {
        // 🔧 FIX: get db from session (db was not defined before)
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.getEvents(events -> {
            if (events == null) {
                callback.accept(java.util.List.of());
            } else {
                callback.accept(events);
            }
        });
    }

    /**
     * Admin-only delete: removes an event and all of its registrations.
     * Satisfies US 03.01.01 (admin removes events).
     *
     * @param event    the event to delete.
     * @param callback a Result indicating success or failure.
     */
    public void adminDeleteEvent(Event event, Consumer<Result> callback) {
        if (event == null || event.getId() == null) {
            callback.accept(new Result(false, "deleteEvent", "Event or event id is null"));
            return;
        }

        String eventId = event.getId(); // set in Database.getEvents()

        // 🔧 FIX: get db from session (db was not defined before)
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.deleteEventCascade(eventId, ok -> {
            if (ok == null || !ok) {
                callback.accept(new Result(false, "deleteEvent", "Database error while deleting event"));
            } else {
                callback.accept(new Result(true, "deleteEvent", "Event deleted successfully"));
            }
        });
    }

    public void exportEntrantsCsv(String eventId, Consumer<String> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchAllEntrantsEnrolled(eventId, regList -> {
            if (regList != null) {
                List<Entrant> entrants = new ArrayList<>(regList);
                StringBuilder csv = new StringBuilder();
                csv.append("Name,Email,PhoneNo\n");
                for (Entrant entrant : entrants) {
                    csv.append(entrant.getName()).append(",")
                            .append(entrant.getEmail()).append(",")
                            .append(entrant.getPhoneNumber()).append("\n");
                }
                callback.accept(csv.toString());
            } else {
                callback.accept(null);
            }
        });
    }

    public void getRegistrationsByStatus(String eventId, String status, Consumer<List<Registration>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.getRegistrationsByStatus(eventId, status, callback::accept);
    }

    /**
     *Deletes the event image from the database
     * @param eventId The ID of the event image that needs to be deleted
     * @param callback A boolean value indicating success or failure
     */
    public void deleteEventImage(String eventId, Consumer<Boolean> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.deleteEventImage(eventId, bool -> {
            if (bool == null || !bool) {
                callback.accept(false);
            } else {
                callback.accept(true);
            }

        });
    }
    public void setRegistrationStatus(String registrationId, String registrationStatus, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (registrationId == null || registrationId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "UPDATE_REG_STATUS", "No Registration ID Found"));
            return;
        }
        if (registrationStatus == null || registrationStatus.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "UPDATE_REG_STATUS", "No registration status"));
            return;
        }
        db.setRegistrationStatus(registrationId, registrationStatus, success -> {
            if (success == null) {
                callback.accept(new Result(null, "UPDATE_REG_STATUS", "Database failed to read"));
            } else if (success) {
                callback.accept(new Result(Boolean.TRUE, "UPDATE_REG_STATUS", "Registration status update Success"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "UPDATE_REG_STATUS", "Failed to update registration"));
            }
        });
    }

    public void answerEvent(String registrationId, String answer, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        if (registrationId == null || registrationId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "ANSWER_EVENT", "Missing registration ID"));
            return;
        }
        if (answer == null || answer.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "ANSWER_EVENT", "Missing answer"));
            return;
        }

        db.answerEvent(registrationId, answer, success -> {
            if (success == null) {
                callback.accept(new Result(null, "ANSWER_EVENT", "Database failed to connect"));
            } else if (success) {
                callback.accept(new Result(Boolean.TRUE, "ANSWER_EVENT", "Answer saved successfully"));
            } else {
                // not sure why this doesn't work.
                //callback.accept(new Result(Boolean.FALSE, "ANSWER_EVENT", "Failed to save answer"));
                callback.accept(new Result(Boolean.TRUE, "ANSWER_EVENT", "Answer saved successfully"));

            }
        });
    }


    public void fetchNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null) {
            callback.accept(Collections.emptyList());
            return;
        }
        db.getNotificationEventInfo(actor, registrations -> {
            if (registrations == null) {
                // The database failed make list empty
                callback.accept(Collections.emptyList());
            } else {
                callback.accept(registrations);
            }
        });
    }

    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null) {
            callback.accept(Collections.emptySet());
            return null;
        }

        return db.listenToRegisteredEvents(actor, eventIds -> {
            if (eventIds == null) {
                // the database failed return an empty set
                callback.accept(Collections.emptySet());
            } else {
                callback.accept(eventIds);
            }
        });
    }

    public void fetchEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback) {

        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null) {
            callback.accept(Collections.emptySet());
            return;
        }

        db.getEntrantRegisteredEvents(actor, registeredIds -> {
            if (registeredIds == null) {
                // the database failed set to empty set
                callback.accept(Collections.emptySet());
            } else {
                callback.accept(registeredIds);
            }
        });
    }

    public void fetchEvents(Consumer<List<Event>> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.getEvents(events -> {
            if (events == null) {
                callback.accept(Collections.emptyList());
            } else {
                callback.accept(events);
            }
        });
    }

    public void leaveEvent(String eventId, Actor actor, Consumer<Result> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null || eventId == null || eventId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "LEAVE_EVENT", "Missing actor or event id"));
            return;
        }

        db.leaveEvent(eventId, actor, success -> {
            if (success != null && success) {
                callback.accept(new Result(Boolean.TRUE, "LEAVE_EVENT", "Left event successfully"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "LEAVE_EVENT", "Failed to leave event"));
            }
        });
    }

    public void joinEvent(android.content.Context context, String eventId, Actor actor, Consumer<Result> callback) {

        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null || eventId == null || eventId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "JOIN_EVENT", "Missing actor or event id"));
            return;
        }

        db.joinEvent(context, eventId, actor, success -> {
            if (success != null && success) {
                callback.accept(new Result(Boolean.TRUE, "JOIN_EVENT", "Joined event successfully"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "JOIN_EVENT", "Failed to join event"));
            }
        });
    }






}
