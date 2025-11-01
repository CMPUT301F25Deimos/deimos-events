package com.example.deimos_events;

public class Session {
    private Database database;
    private final Result result = new Result(false, "");
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

    public void updateResult(Boolean cond, String message){
        result.set(cond, message); // updates current result
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
}
