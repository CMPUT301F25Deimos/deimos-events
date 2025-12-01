package com.example.deimos_events;

import static org.junit.jupiter.api.Assertions.*;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.dataclasses.Result;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.SessionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        Actor actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        testSession.setCurrentActor(actor);
        AM.insertActor(actor, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Insert should work if Session has the Actor and the Database doesn't");
        assertEquals(actor, testSession.getCurrentActor());
    }
    @Test

    void testDoubleInsertInDatabase(){

        Actor actor_1 = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
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
        Actor actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        testSession.setCurrentActor(actor);
        mdb.insertActor(actor, r ->{}); // place actor in database
        AM.deleteActor(actor, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Delete should work if actor not null and exists in Database");
    }

    @Test
    void testDoubleDeleteInDatabase(){
        Actor actor_1 = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(actor_1, r ->{}); // place actor in database
        mdb.deleteActor(actor_1, r->{}); // remove actor from database
        testSession.setCurrentActor(actor_1); // place actor into session
        AM.deleteActor(actor_1, resultCapturer); // this will try to delete the actor in the session but not in the database
        assertFalse(resultCapturer.get().getCond(), "Delete should fail when actor is not in database");
    }

    @Test
    void testFetchActorByID(){
        Actor actor_1 = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(actor_1, r ->{}); // place actor in database
        AM.fetchActorByID(actor_1.getDeviceIdentifier(), r->{}); // Now should be in session
        assertEquals(actor_1, testSession.getCurrentActor());
    }

    @Test
    void testFetchActorRoleNullActor() {
        AM.fetchActorRole(null, resultCapturer);
        Result r = resultCapturer.get();
        assertFalse(r.isSuccess(), "Expected failure when actor is null");
        assertEquals("FETCH_ROLE", r.getOperation(), "Result type should be FETCH_ROLE");
        assertEquals("Actor not given", r.getMessage());
    }


    @Test
    void testUpdateActor(){
        Actor old_actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(old_actor, r->{}); // place the actor
        Actor updated_actor = new Actor("9ADXE3", "mike", "noemail@gmail.com", "119", false);
        AM.updateActor(old_actor, updated_actor, resultCapturer);
        assertEquals(updated_actor, testSession.getCurrentActor());
    }
    @Test
    void testActorExistsByEmail(){
        Actor old_actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(old_actor, r->{}); // place the actor
        AM.actorExistsByEmail("myemail@gmail.com", resultCapturer);
        assertTrue(resultCapturer.get().isSuccess());
    }
    @Test
    void testActorExistsByEmailFails(){
        Actor actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(actor, r->{}); // place the actor
        AM.actorExistsByEmail("invalid@gmail.com", resultCapturer);
        assertFalse(resultCapturer.get().isSuccess());
    }

    @Test
    void testActorExistsByIdFound() {
        Actor actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        mdb.insertActor(actor, r -> {}); // put actor in mock DB

        final Actor[] captured = new Actor[1];

        AM.actorExistsByid("9ADXE3", found -> captured[0] = found);

        assertNotNull(captured[0], "Expected actor to be found by ID");
        assertEquals(actor, captured[0], "Returned actor should match the one in DB");
    }

    @Test
    void testGetAllActorsNonEmptyDatabase() {
        Actor a1 = new Actor("9ADXE3", "Daveed", "Daveed@gmail.com", "911", false);
        Actor a2 = new Actor("9APPP3", "mary", "mary@gmail.com", "922", false);
        mdb.insertActor(a1, r -> {});
        mdb.insertActor(a2, r -> {});

        final List<Actor>[] captured = new List[1];

        AM.getAllActors(list -> captured[0] = list);

        assertNotNull(captured[0], "List should not be null");
        assertEquals(2, captured[0].size(), "Expected two actors");
        assertTrue(captured[0].contains(a1));
        assertTrue(captured[0].contains(a2));
    }


    @Test
    void testDeleteEntrantCascade(){
        Actor actor = new Actor("9ADXE3", "Daveed", "myemail@gmail.com", "911", false);
        Event swimmingEvent = new Event("83A", "swimming", "swimming_pic333", "climbingEvent where you swim", "today", 10,  false, "12NDKA", "michael", "stuff", "More stuff");
        Event climbingEvent = new Event("97F", "climbing", "climbing_mock", "climbingEvent where you climb", "tomorrow", 20,  true, "90XHAD", "Jorge", "stuff", "More Stuff");
        Registration swimRegistration = new Registration("ignored", "9ADXE3", "83A", "Pending", "38.8951", "77.0364");
        Registration climbRegistration = new Registration("ignored", "9ADXE3", "97F", "Accepted","38.8951", "77.0364");
        mdb.insertActor(actor, r ->{});
        mdb.insertEvent(climbingEvent, r -> {});
        mdb.insertRegistration(swimRegistration, r-> {});
        mdb.insertRegistration(climbRegistration, r-> {});
        AM.deleteEntrantCascade(actor, resultCapturer);
        assertTrue(resultCapturer.get().isSuccess());
    }



}
