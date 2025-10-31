package com.example.deimos_events;

public class Session {
    private Database database;
    private Result result;
    private Actor currentActor;
    private Actor selectedActor;
    public Session(Database database){
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public Actor getSelectedActor() {
        return selectedActor;
    }
}
