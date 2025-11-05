package com.example.deimos_events;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.util.function.Consumer;
public class ActorManager {

    private final SessionManager sessionManager;

    public ActorManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

    }
    public void deleteActor(Consumer<Result> callback) {


        // Validate what you are trying to do, before querying the database


        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();


        // Validate the query
        db.actorExists(actor, exists -> {
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "Database Failed to Read"));

            }else if (exists){
                db.deleteActor(actor, delResult -> {
                    if (delResult) {
                        callback.accept(new Result(Boolean.TRUE, "DELETE_ACTOR",  "Successfully Deleted user"));
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "Failed to delete user"));
                    }
                });
            }else{
                callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "Actor  does not exist"));
            }
        });
    }
    public void createActor(Consumer<Result> callback) {
        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        // Validate what you are trying to do, before querying the database

        // Validate the query
        db.actorExists(actor, exists ->{
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Database Failed to Read"));
            }else if (exists){
                callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Actor already exists"));
            }else{
                db.createActor(actor, createResult -> {
                    if (createResult) {
                         callback.accept(new Result(Boolean.TRUE, "CREATE_ACTOR",  "Successfully created user"));
                    } else {
                       callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Failed to write user"));
                    }
                });
            }
        });
    }

    /*
    public void createActor(Context context, String name, String email, String phoneNo) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = new Actor(androidId, name, email, phoneNo);
        db.createActor(actor, success -> {
            if (success) {
                new Result(Boolean.TRUE, "CREATE_ACTOR",  "Successfully created a user");
            } else {
                new Result(Boolean.FALSE, "CREATE_ACTOR",  "Failed To Create User");
            }
        });
    }
    */
}
