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
     * Checks whether an {@link Actor} with the given email exists in the Database
     * <p>
     * Wrapper around {@link Database#actorExistsByEmail(String, Consumer)} and converts its callback
     * into a result callback
     * <p>
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the actor was found</li>
     *     <li>{@code cond = false} if the operation failed, or no actor with such an Email was found
     *     </li>
     *     <li>{@code type = "ACTOR_EXISTS"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param email The email we are searching for. if {@code null}, callback gets a failure {@link Result}
     * <p>
     *
     */
    public void actorExistsByEmail(String email, Consumer<Result> callback){
       Session session = sessionManager.getSession();
       IDatabase db = session.getDatabase();
        if (email == null){
            callback.accept(new Result(Boolean.FALSE, "ACTOR_EXISTS", "No email provided"));
            return;
        }
        // Validate the query
        db.actorExistsByEmail(email, res ->{
            if (res == null) {
                callback.accept(new Result(null, "ACTOR_EXISTS", "Network Error"));
            } else if (res) {
                callback.accept(new Result(true, "ACTOR_EXISTS", "Email Found Successfully"));
            } else {
                callback.accept(new Result(false, "ACTOR_EXISTS", "Email not found"));
            }
        });
    }

    /**
     * This method will attempt to insert the current {@link Actor} into the Database.
     * <p>
     * This method does some validation before issuing the insertion request to the database
     * <p>
     * Verifies that the current Actor is not present in the Database.
     * <p>
     * If the Actor is not present in the database it will send an insert request via
     * {@code Database.insertActor()}
     * <p>
     *
     * On Success will add the actor to the {@link Session}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the insertion succeeded</li>
     *     <li>{@code cond = false} if the operation failed or the actor was already found in the database
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
    public void insertActor(Actor actor, Consumer<Result> callback) {
        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "No Actor found"));
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
                        session.setCurrentActor(actor); // add actor to session.
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "INSERT_ACTOR", "Failed to write user"));
                    }
                });
            }
        });
    }
    /**
     * This method will attempt to update the current {@link Actor} from the Database.
     * <p>
     * This method does some validation before issuing the update request to the database, and then
     * updates the session with the changed actor
     * <p>
     * Checks if the actor is not null
     * <p>
     * Checks if the Actor exists in the database using {@code Database.actorExists()}
     * <p>
     * If the Actor exists it sends a delete request via {@code Database.updateActor()}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the updates succeeds </li>
     *     <li>{@code cond = false} if the operation failed or the actor was not found</li>
     *     <li>{@code type = "UPDATE_ACTOR"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param callback a {@link Consumer} that receives a {@link Result} which represents the
     *                 outcome of the update attempt. The callback is always invoked even if the
     *                 database is not queried.
     *
     * @see Session
     * @see Actor
     * @see Database#updateActor(Actor, Actor, Consumer)
     * @see Database#actorExists(Actor, Consumer)
     */
    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Result> callback){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // validate what you are trying to do, before querying the database

        if (oldActor == null || updatedActor == null){
            callback.accept(new Result(Boolean.FALSE, "UPDATE_ACTOR", "No actor found"));
            return;
        }
        // Validate the query
        db.actorExists(oldActor, exists -> {
            if (exists == null){
                callback.accept(new Result(Boolean.FALSE, "UPDATE_ACTOR", "Database Failed to Read"));

            }else if (exists){
                db.updateActor(oldActor, updatedActor, upResult -> {
                    if (upResult) {
                        callback.accept(new Result(Boolean.TRUE, "UPDATE_ACTOR",  "Successfully Updated user"));
                        // update the session
                        session.setCurrentActor(updatedActor);
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "UPDATE_ACTOR", "Failed to update user"));
                    }
                });
            }else{
                callback.accept(new Result(Boolean.FALSE, "UPDATE_ACTOR", "Actor  does not exist"));
            }
        });
    }

    /**
     * This method will attempt to fetch the current {@link Actor} defined by the ID and place it
     * Into the {@link Session} object.
     * <p>
     * If the Actor is not present in the database it will not modify the session object
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the fetch succeeded</li>
     *     <li>{@code cond = false} if the operation failed, or no actor with such an ID was found
     *     </li>
     *     <li>{@code type = "FETCH_ACTOR"} which identifies the result type</li>
     *     <li>{@code message} which describes the specific outcome or failure reason</li>
     * </ul>
     *
     * @param callback a {@link Consumer} that receives a {@link Result} which represents the
     *                 outcome of the fetch attempt.
     * @see Session
     * @see Actor
     * @see Database#fetchActorByID(String, Consumer)
     */
    public void  fetchActorByID(String id, Consumer<Result> callback){
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        db.fetchActorByID(id, actor ->{
            if (actor != null){
                session.setCurrentActor(actor);
                callback.accept(new Result(true, "FETCH_ACTOR", "Actor was fetched successfully"));
            } else {
                callback.accept(new Result(false, "FETCH_ACTOR", "Actor not found"));
            }
        });
    }

    /**
     * This method will attempt to delete the current {@link Actor} from the Database.
     * <p>
     * This method does some validation before issuing the delete request to the database, and then
     * removing the actor from the Session object
     * <p>
     * Checks if the actor is not null
     * <p>
     * Checks if the Actor exists in the database using {@code Database.actorExists()}
     * <p>
     * If the Actor exists it sends a delete request via {@code Database.deleteActor()}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the deletion succeeded</li>
     *     <li>{@code cond = false} if the operation failed or the actor was not found</li>
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
    public void deleteActor(Actor actor, Consumer<Result> callback) {

        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "No Actor found"));
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
                        // remove from the session
                        session.setCurrentActor(null);
                    } else {
                        callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "Failed to delete user"));
                    }
                });
            }else {
                callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "Actor  does not exist"));
            }
        });
    }



    /**
     * This method will attempt to delete an {@link Entrant} from the Database and delete all
     * documents that reference them
     * <p>
     * This method does some validation before issuing the delete request to the database, DOES NOT
     * UPDATE THE SESSION OBJECT. This must be done by the caller
     * <p>
     * Checks if the actor is not null
     * <p>
     * Checks if the Actor exists in the database using {@code Database.actorExists()}
     * <p>
     * If the Actor exists it sends a delete request via {@code Database.deleteEntrantCascade()}
     * <p>
     *
     * This operation talks to the database and as such is asynchronous.
     * The {@link Result} object will contain the following on completion of the callback
     * <ul>
     *     <li>{@code cond = true} if the deletion succeeded</li>
     *     <li>{@code cond = false} if the operation failed or the actor was not found</li>
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
     * @see Database#deleteEntrantCascade(String, Consumer)
     * @see Database#actorExists(Actor, Consumer)
     */
    public void deleteEntrantCascade(Actor actor, Consumer<Result> callback) {

        // grab session, database, and what you need, in this case the actor
        Session session = sessionManager.getSession();
        IDatabase db = session.getDatabase();

        // Validate what you are trying to do, before querying the database
        if (actor == null){
            callback.accept(new Result(Boolean.FALSE, "DELETE_ACTOR", "No Actor found"));
            return;
        }

        // Validate the query
        db.actorExists(actor, exists -> {
            if (exists == null){
                callback.accept(new Result(null, "DELETE_ACTOR", "Database Failed to Read"));

            }else if (exists){
                db.deleteEntrantCascade(actor.getDeviceIdentifier(), delResult -> {
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
}
