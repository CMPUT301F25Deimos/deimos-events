package com.example.deimos_events;
import android.app.Application;

import com.example.deimos_events.managers.SessionManager;


/**
 * A custom version of the {@link Application} subclass
 * <p>
 * This is created once the app starts and is used to setup and store the shared
 * instance of the {@link SessionManager}.
 * <p>
 * All Activities access the session manager through the method {@link #getSessionManager()}
 * instead of creating their own. This makes sure that throughout the application there is a single persistent
 * session and single instance of each manager
 */

public class EventsApp extends Application {
    private SessionManager sessionManager;

    /**
     *  Initializes the global application state
     *  <p>
     *  Sets up the {@link SessionManager} that is shared throughout the apps lifetime
     */
    @Override
    public void onCreate(){
        super.onCreate();
        sessionManager = new SessionManager();
    }

    /**
     * Returns the shared {@link SessionManager} instance for this application
     * @return The session manager used by acitviies to access session data and each manager.
     */
    public SessionManager getSessionManager(){
        return sessionManager;
    }
}
