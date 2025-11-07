package com.example.deimos_events.managers;


import com.example.deimos_events.Notification;
import com.example.deimos_events.Session;

/**
 * Handles persistent operations connected to the {@link Notification} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Notification} objects temporarily, but all persistent operations
 * must be performed via the {@code NotificationManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class NotificationManager {
    private final SessionManager sessionManager;

    public NotificationManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
}
