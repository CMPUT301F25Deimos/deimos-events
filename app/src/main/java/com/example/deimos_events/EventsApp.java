package com.example.deimos_events;
import android.app.Application;

import com.example.deimos_events.managers.SessionManager;

public class EventsApp extends Application {
    private SessionManager sessionManager;

    @Override
    public void onCreate(){
        super.onCreate();
        sessionManager = new SessionManager();
    }

    public SessionManager getSessionManager(){
        return sessionManager;
    }
}
