package com.example.deimos_events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

public class ActorManagerTest {
    private MockDatabase mdb;
    private Session testSession;
    private ActorManager AM;

    private ResultCapturer resultCapturer;

    private final Consumer<Result> NO_OP = result -> {};

    @BeforeEach
    void init(){
        resultCapturer = new ResultCapturer();
        SessionManager testSessionManager = new SessionManager(false);
        mdb = new MockDatabase();
        testSession = new Session(mdb);
        testSessionManager.setSession(testSession);
        AM = new ActorManager(testSessionManager);
    }

    @Test
    void testInsertActorNullActor(){
        Actor actor = null;
        AM.insertActor(actor, resultCapturer);
        assertFalse(resultCapturer.get().getCond(), "Insert Should Fail if Actor is null");
    }
    @Test
    void testInsertActorInDatabase(){
        Actor actor = new Actor("123", "john", "myemail@gmail.com", "911");
        testSession.setCurrentActor(actor);
        AM.insertActor(actor, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Insert should work if Session has the Actor and the Database doesn't");
    }
    @Test

    void testDoubleInsertInDatabase(){

        Actor actor_1 = new Actor("123", "john", "myemail@gmail.com", "911");
        mdb.insertActor(actor_1, r ->{}); // place actor in database
        testSession.setCurrentActor(actor_1); // set our current actor we care about
        AM.insertActor(actor_1, resultCapturer); // this will try to insert actor
        assertFalse(resultCapturer.get().getCond(), "Insert Should Fail for Duplicate Insertions");
    }

    @Test
    void testDeleteNullActor(){
        Actor actor = null;
        AM.deleteActor(actor, resultCapturer);
        assertFalse(resultCapturer.get().getCond(), "Delete Should Fail if we try to delete a null actor");
    }
    @Test
    void testDeleteActorInDatabase(){
        Actor actor = new Actor("123", "john", "myemail@gmail.com", "911");
        testSession.setCurrentActor(actor);
        mdb.insertActor(actor, r ->{}); // place actor in database
        AM.deleteActor(actor, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Delete should work if actor not null and exists in Database");
    }

    @Test
    void testDoubleDeleteInDatabase(){
        Actor actor_1 = new Actor("123", "john", "myemail@gmail.com", "911");
        mdb.insertActor(actor_1, r ->{}); // place actor in database
        mdb.deleteActor(actor_1, r->{}); // remove actor from database
        testSession.setCurrentActor(actor_1); // place actor into session
        AM.deleteActor(actor_1, resultCapturer); // this will try to delete the actor in the session but not in the database
        assertFalse(resultCapturer.get().getCond(), "Delete should fail when actor is not in database");
    }

    @Test
    void testFetchActorByID(){
        Actor actor_1 = new Actor("123", "john", "myemail@gmail.com", "911");
        mdb.insertActor(actor_1, r ->{}); // place actor in database
        AM.fetchActorByID(actor_1.getDeviceIdentifier(), r->{}); // Now should be in session
        assertEquals(actor_1, testSession.getCurrentActor());
    }
}
