package com.example.deimos_events.managers;

import android.app.Activity;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Event;

/**
 * This Manager gives access to UI data stored in the Session object to Activities and Fragments
 * <p>
 *  Acts to isolate User Interface Layer from the Apps data layer.
 * </p>
 */
public class UserInterfaceManager {
    private final SessionManager sessionManager;
    public UserInterfaceManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }


    public Actor getCurrentActor(){
        return sessionManager.getSession().getCurrentActor();
    }
    public void setCurrentActor(Actor actor){
        sessionManager.getSession().setCurrentActor(actor);
    }

    public void clearCurrentActor(){
        setCurrentActor(null);
    }
    public Event getCurrentEvent(){
        return sessionManager.getSession().getCurrentEvent();
    }





}
