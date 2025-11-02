package com.example.deimos_events;

import android.app.Activity;

public class UserInterfaceManager {
    private final SessionManager sessionManager;
    private final NavigationManager navigationManager = new NavigationManager(this);

    public UserInterfaceManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }


    public Actor  getActorFromSessionManager(){
        return sessionManager.getSession().getCurrentActor();
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public Activity getActivity(){
        return sessionManager.getSession().getActivity();
    }


}
