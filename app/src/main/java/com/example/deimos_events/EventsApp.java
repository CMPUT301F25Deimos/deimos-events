package com.example.deimos_events;
import android.app.Application;
public class EventsApp extends Application {
    private SessionManager sessionManager;

    @Override
    public void onCreate(){
        super.onCreate();
        sessionManager = new SessionManager();

        Actor actor = new Actor("123", "David", "DavidMartinez@gmail.com", "999");
        sessionManager.getSession().setCurrentActor(actor);
    }

    public SessionManager getSessionManager(){
        return sessionManager;
    }
}
