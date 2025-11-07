package com.example.deimos_events.managers;

import android.app.Activity;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Event;

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
