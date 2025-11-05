package com.example.deimos_events;

import android.app.Activity;

public class UserInterfaceManager {
    private final SessionManager sessionManager;
    private final NavigationManager navigationManager = new NavigationManager(this);

    public UserInterfaceManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }


    public Actor getCurrentActor(){
        return sessionManager.getSession().getCurrentActor();
    }
    public void setCurrentActor(Actor actor){
        sessionManager.getSession().setCurrentActor(actor);
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public Activity getActivity(){
        return sessionManager.getSession().getActivity();
    }


}
