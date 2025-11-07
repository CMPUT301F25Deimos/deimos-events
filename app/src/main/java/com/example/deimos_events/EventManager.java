package com.example.deimos_events;
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

    public void getEventById(String eventId, Consumer<Event> callback){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getEventById(eventId, callback);
    }



}
