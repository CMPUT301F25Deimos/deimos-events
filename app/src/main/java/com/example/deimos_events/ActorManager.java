package com.example.deimos_events;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class ActorManager {

    private final SessionManager sessionManager;

    public ActorManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

    }
    public void deleteActor() {


        // Validate what you are trying to do, before querying the database


        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();


        // Validate the query
        db.actorExists(actor, e ->{
            if (e == null){
                sessionManager.updateResult(Boolean.FALSE, "DELETE_ACTOR", "Database Failed to Read");
            }else if (e){
                db.deleteActor(actor, success -> {
                    if (success) {
                        sessionManager.updateResult(Boolean.TRUE, "DELETE_ACTOR",  "Successfully Deleted user");
                    } else {
                        sessionManager.updateResult(Boolean.FALSE, "DELETE_ACTOR", "Failed to delete user");
                    }
                });
            }else{
                sessionManager.updateResult(Boolean.FALSE, "DELETE_ACTOR", "Actor  does not exist");
            }
        });
    }




    public void createActor() {
        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        // Validate what you are trying to do, before querying the database



        // Validate the query
        db.actorExists(actor, e ->{
            if (e == null){
                sessionManager.updateResult(Boolean.FALSE, "CREATE_ACTOR", "Database Failed to Read");
            }else if (e){
                sessionManager.updateResult(Boolean.FALSE, "CREATE_ACTOR", "Actor already exists");
            }else{
                db.createActor(actor, success -> {
                    if (success) {
                        sessionManager.updateResult(Boolean.TRUE, "CREATE_ACTOR",  "Successfully created user");
                    } else {
                        sessionManager.updateResult(Boolean.FALSE, "CREATE_ACTOR", "Failed to write user");
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
                sessionManager.updateResult(Boolean.TRUE, "CREATE_ACTOR",  "Successfully created a user");
            } else {
                sessionManager.updateResult(Boolean.FALSE, "CREATE_ACTOR",  "Failed To Create User");
            }
        });
    }
    */
}
