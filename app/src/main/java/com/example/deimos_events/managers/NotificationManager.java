package com.example.deimos_events.managers;


import com.example.deimos_events.IDatabase;
import com.example.deimos_events.Notification;
import com.example.deimos_events.Registration;
import com.example.deimos_events.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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
    public void getNotification(String orgId, Consumer<List<String>> Descritption){
       IDatabase db =  sessionManager.getSession().getDatabase();
       db.getNotificationOrgId(orgId ,callback->{
           Set<String> uniqueSet = new HashSet<>();
           for (Registration r: callback){
               uniqueSet.add(r.description);
           }
           List<String> uniqueList = new ArrayList<>(uniqueSet);
           Descritption.accept(uniqueList);
       });
        }
    }

