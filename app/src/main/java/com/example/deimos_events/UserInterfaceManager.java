package com.example.deimos_events;

public class UserInterfaceManager {
    private final SessionManager sessionManager;
    private final NavigationManager navigationManager = new NavigationManager(this);

    public UserInterfaceManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public Result getResult(){

        return sessionManager.getSession().getResult();

    }
}
