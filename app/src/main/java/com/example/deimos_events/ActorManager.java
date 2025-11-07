package com.example.deimos_events;

import java.util.List;
import java.util.function.Consumer;

/**
 * Handles persistent operations connected to the {@link Actor} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Actor} objects temporarily, but all persistent operations
 * must be performed via the {@code ActorManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class ActorManager {

    private final SessionManager sessionManager;

    public ActorManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ActorManager(){
        this.sessionManager = null;
    }
    public void deleteActor(Consumer<Result> callback) {


        // Validate what you are trying to do, before querying the database


        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();


        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "No Actor in Session"));
            return;
        }


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

    public void insertActor(Consumer<Result> callback) {
        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "No Actor in Session"));
            return;
        }

        // Validate the query
        db.actorExists(actor, exists ->{
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Database Failed to Read"));
            }else if (exists){
                callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Actor already exists"));
            }else{
                db.insertActor(actor, createResult -> {
                    if (createResult) {
                         callback.accept(new Result(Boolean.TRUE, "CREATE_ACTOR",  "Successfully created user"));
                    } else {
                       callback.accept(new Result(Boolean.FALSE, "CREATE_ACTOR", "Failed to write user"));
                    }
                });
            }
        });
    }
    public void getActorById(String id, Consumer<Actor> callback) {
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        db.getActorById(id, actor -> {
            if (actor == null) {
                callback.accept(null);
            }
            else {
                callback.accept(actor);
            }

        });
    }


    public void actorExistsByEmail(Consumer<Result> callback){

    }

    /* Temporarily commented out.
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

//    public void getAvailableEvents(Consumer<List<Event>> callback) {
//        Session session = sessionManager.getSession();
//        IDatabase db = session.getDatabase();
//        Actor actor = session.getCurrentActor();
//        db.getAvailableEvents(actor, callback);
//    }
}
