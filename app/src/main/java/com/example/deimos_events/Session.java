package com.example.deimos_events;

import android.app.Activity;

/**
 * Used to represent the current application state, acting as a snapshot of the data that is
 * shared between managers and activities
 * <p>
 * The {@code Session} Contains no UI elements. It stores context relevant information, for example
 * The current {@link Actor} or current {@link Event} being viewed or edited.
 * It also exists to reduce unnecessary database queries
 * <p>
 * The session should hold only context specific objects. UI displays should be maanaged within their
 * own activity, fragment or viewmodel.
 * <p>
 *
 * The {@code Session} does not perform database operations. Any data it holds must be saved,
 * updated, or deleted by the appropriate manager.
 */

public class Session {
    private IDatabase database;
    private Actor currentActor;
    private Event currentEvent;
    private Result result;


    private Activity activity;

    public Session(IDatabase database) {
            this.database = database;
        }

        public IDatabase getDatabase() {
            return database;
        }

        public Actor getCurrentActor () {
            return currentActor;
        }

        public void setCurrentActor (Actor actor){
            this.currentActor = actor;
        }
         public void setCurrentEvent(Event event) {
             this.currentEvent= event;
         }

        public Activity getActivity () {
            return activity;
        }
        public void setActivity (Activity activity){
            this.activity = activity;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public Result getResult() {
            return result;
        }
        public Event getCurrentEvent(){return currentEvent;}
}

