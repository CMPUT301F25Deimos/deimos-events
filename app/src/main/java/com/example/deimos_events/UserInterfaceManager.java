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

    public void attachResultListener(ResultListener resultListener){
        Result result = sessionManager.getSession().getResult();
        result.addListener(resultListener);
    }

    public void clearResultListener(){
        Result result = sessionManager.getSession().getResult();
        result.

    }
}
