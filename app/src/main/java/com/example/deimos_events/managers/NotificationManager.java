package com.example.deimos_events.managers;


import com.example.deimos_events.IDatabase;
import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.Session;
import com.example.deimos_events.dataclasses.Result;
import com.example.deimos_events.ui.notifications.NotificationsArrayAdapter;
import com.example.deimos_events.ui.notifications.NotificationsAdminArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handles persistent operations connected to the {@link Notifications} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Notifications} objects temporarily, but all persistent operations
 * must be performed via the {@code NotificationManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class NotificationManager {
    private final SessionManager sessionManager;

    public NotificationManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void insertNotifications(String sender, String recipientId, String message, String eventId, String registrationId, Consumer<Result> callback) {

        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // Validate before calling database
        if (sender == null || sender.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_NOTIFICATION", "Missing sender ID"));
            return;
        }
        if (recipientId == null || recipientId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_NOTIFICATION", "Missing recipient ID"));
            return;
        }
        if (eventId == null || eventId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_NOTIFICATION", "Missing event ID"));
            return;
        }
        if (registrationId == null || registrationId.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_NOTIFICATION", "Missing registration ID"));
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_NOTIFICATION", "Missing message"));
            return;
        }

        db.setNotifications(sender, recipientId, message, eventId, registrationId, success -> {
            if (success == null) {
                callback.accept(new Result(null,
                        "INSERT_NOTIFICATION",
                        "Database failed to connect"));
            } else if (success) {
                callback.accept(new Result(Boolean.TRUE,
                        "INSERT_NOTIFICATION",
                        "Notification inserted successfully"));
            } else {
                callback.accept(new Result(Boolean.FALSE,
                        "INSERT_NOTIFICATION",
                        "Failed to insert notification"));
            }
        });
    }

    public void fetchNotifications(Actor actor,ArrayList<Notifications> notificationsList, NotificationsArrayAdapter adapter){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        if (actor == null) {
            notificationsList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        db.getNotifications(actor, notificationsList, adapter);
    }
        /**
         * Gets the receivers for a notification
         * <p>
         * This is a thin wrapper around the database-level implementation.
         * @param eventId the event we are interested in
         * @param recipients The lists of recipients
         * @param callback used for error messaging
         */
        public void fetchNotificationReceivers(String eventId, List<String> recipients, Consumer<List<Map<String, String>>> callback) {
            Session session = sessionManager.getSession();
            IDatabase db = session.getDatabase();
            // invalid eventId means no receivers
            if (eventId == null || eventId.trim().isEmpty()) {
                callback.accept(Collections.emptyList());
                return;
            }
            // Delegate to database
                // if null we have an empty list
                // if not null we pass
            db.getNotificationReceivers(eventId, recipients, receivers->{
                if (receivers == null){
                    callback.accept(Collections.emptyList());
                    return;
                }
                callback.accept(receivers);
            });
        }

    public void insertNotificationsPreference(Actor actor, boolean preference, Consumer<Result> callback) {

        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        if (actor == null) {
            callback.accept(new Result(Boolean.FALSE, "INSERT_PREFERENCE", "No actor provided"));
            return;
        }

        db.setNotificationsPreference(actor, preference, success -> {
            if (success == null) {
                callback.accept(new Result(null, "INSERT_PREFERENCE", "Database failed to connect"));
            } else if (success) {
                callback.accept(new Result(Boolean.TRUE, "INSERT_PREFERENCE", "Preference updated successfully"));
            } else {
                callback.accept(new Result(Boolean.FALSE, "INSERT_PREFERENCE", "Failed to update preference"));
            }
        });
    }
    public void fetchNotificationsPreference(Actor actor, Consumer<Boolean> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        if (actor == null) {
            callback.accept(Boolean.FALSE);
            return;
        }
        db.getNotificationsPreference(actor, callback);
    }




}

