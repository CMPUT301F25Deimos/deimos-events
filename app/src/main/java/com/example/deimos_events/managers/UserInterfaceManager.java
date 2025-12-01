package com.example.deimos_events.managers;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Event;

/**
 * This Manager gives access to UI data stored in the Session object to Activities and Fragments
 * <p>
 * Acts to isolate User Interface Layer from the Apps data layer.
 * </p>
 */
public class UserInterfaceManager {
    private final SessionManager sessionManager;
    public UserInterfaceManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    /**
     * Returns the currently active {@link Actor} from the session
     * @return Actor instance
     * @see Actor
     */
    public Actor getCurrentActor() {
        return sessionManager.getSession().getCurrentActor();
    }
    /**
     * Sets the active {@link Actor} in the session.
     * @param actor which is the actor to set as the current actor
     */
    public void setCurrentActor(Actor actor) {
        sessionManager.getSession().setCurrentActor(actor);
    }
    /**
     * Clears the current actor from the session.
     */
    public void clearCurrentActor() {
        setCurrentActor(null);
    }
    /**
     * Returns the currently selected {@link Event} stored in the session.
     * @return Event instance
     */
    public Event getCurrentEvent() {
        return sessionManager.getSession().getCurrentEvent();
    }


}
