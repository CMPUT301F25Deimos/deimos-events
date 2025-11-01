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

    public Result getResult(){
        return sessionManager.getSession().getResult();
    }

    public void attachResultListener(ResultListener resultListener){
        sessionManager.getSession().getResult().addResultListener(resultListener);
    }

    public void clearResult(){
        sessionManager.getSession().getResult().clear();
    }
    public void clearResultListener(){
        sessionManager.getSession().getResult().removeResultListener();
    }
}
