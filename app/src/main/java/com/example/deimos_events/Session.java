package com.example.deimos_events;

import android.app.Activity;

public class Session {
    private Database database;
    private Result result = new Result();
    private Actor currentActor;
    private Actor selectedActor;

    private Activity activity;
    public Session(Database database){
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public Result getResult() {
        return result;
    }

    public void updateResult(Boolean cond, String operation, String message){
        result.set(cond, operation, message); // updates current result
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public Actor getSelectedActor() {
        return selectedActor;
    }
    public void setCurrentActor() {
        this.currentActor = null;
    }
    public void setCurrentActor(Actor actor) {
        this.currentActor = actor;
    }

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity){
        this.activity = activity;
    }
}
