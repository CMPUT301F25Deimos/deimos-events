package com.example.deimos_events;

import android.app.Activity;

public class Session {
    private Database database;
    private Actor currentActor;
    private Actor selectedActor;

    private Activity activity;
    public Session(Database database){
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public Actor getSelectedActor() {
        return selectedActor;
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
