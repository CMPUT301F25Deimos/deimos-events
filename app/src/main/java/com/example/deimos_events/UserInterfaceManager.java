package com.example.deimos_events;

import android.app.Activity;

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
    public Activity getActivity(){
        return sessionManager.getSession().getActivity();
    }
    public Event getCurrentEvent(){
        return sessionManager.getSession().getCurrentEvent();
    }





}
