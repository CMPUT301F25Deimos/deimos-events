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


    /**
     * This method will attempt to delete the current {@link Actor} stored in the {@link Session}.
     * <p>
     * This method does some validation before issuing the delete request to the database
     * <p>
     * Verifies that the current Actor is present in the Session.
     * Checks if the Actor exists in the database using {@code Database.actorExists()}
     * If the Actor exists it sends a delete request via {@code Database.deleteActor()}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the deletion succeeded</li>
     *     <li>{@code cond = false} if the operation failed, the actor was not found or the
     *     session contained no such actor</li>
     *     <li>{@code type = "DELETE_ACTOR"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param callback a {@link Consumer} that receives a {@link Result} which represents the
     *                 outcome of the deletion attempt. The callback is always invoked even if the
     *                 database is not queried.
     *
     * @see Session
     * @see Actor
     * @see Database#deleteActor(Actor, Consumer)
     * @see Database#actorExists(Actor, Consumer)
     */
    public void deleteActor(Consumer<Result> callback) {

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
    /**
     * This method will attempt to insert the current {@link Actor} stored in the {@link Session}.
     * <p>
     * This method does some validation before issuing the insertion request to the database
     * <p>
     * Verifies that the current Actor is present in the Session.
     * Verifies that the current Actor is not present in the Database.
     * <p>
     * If the Actor is not present in the database it will send an insert request via
     * {@code Database.insertActor()}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the insertion succeeded</li>
     *     <li>{@code cond = false} if the operation failed, the actor was already found in the database
     *     or the session contained no such actor</li>
     *     <li>{@code type = "INSERT_ACTOR"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param callback a {@link Consumer} that receives a {@link Result} which represents the
     *                 outcome of the insertion attempt. The callback is always invoked even if the
     *                 database is not queried.
     *
     * @see Session
     * @see Actor
     * @see Database#insertActor(Actor, Consumer)
     * @see Database#actorExists(Actor, Consumer)
     */
    public void insertActor(Consumer<Result> callback) {
        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "No Actor in Session"));
            return;
        }

        // Validate the query
        db.actorExists(actor, exists ->{
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "Database Failed to Read"));
            }else if (exists){
                callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "Actor already exists"));
            }else{
                db.insertActor(actor, createResult -> {
                    if (createResult) {
                         callback.accept(new Result(Boolean.TRUE, "INSERT_ACTOR",  "Successfully created user"));
                    } else {
                       callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "Failed to write user"));
                    }
                });
            }
        });
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
