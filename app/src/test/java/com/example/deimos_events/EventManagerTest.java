package com.example.deimos_events;

import static org.junit.jupiter.api.Assertions.*;

import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventManagerTest {
    private MockDatabase mdb;
    private Session testSession;
    private EventManager EM;
    private ResultCapturer resultCapturer;


    @BeforeEach
    void init(){
        resultCapturer = new ResultCapturer();
        SessionManager testSessionManager = new SessionManager(false);
        mdb = new MockDatabase();
        testSession = new Session(mdb);
        testSessionManager.setSession(testSession);
        EM = new EventManager(testSessionManager);
    }

    @Test
        void testInsertEventNull(){
        EM.insertEvent(null, resultCapturer);
        assertFalse(resultCapturer.get().getCond(), "Inserts should fail for null events");
    }

    @Test
    void testInsertEventInDatabase(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23");
        EM.insertEvent(event, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Event should be in the database");
        assertEquals(event, testSession.getCurrentEvent());
    }

    @Test
    void testDoubleInsertEventInDatabase(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23");
        mdb.insertEvent(event, r->{}); // insert once
        EM.insertEvent(event, resultCapturer); // insert twice
        assertFalse(resultCapturer.get().isSuccess(), "Inserts Should fail if event is already in database");
    }

    @Test
    void testDeleteRegistration(){
        Registration registration = new Registration("9ADMC", "3981", "9851", "Pending");
        mdb.insertRegistration(registration, r->{}); //
        EM.deleteRegistration(registration, resultCapturer);
        assertTrue(resultCapturer.get().isSuccess(), "Delete should succeed on deleting existent registrations");
    }
    @Test
    void testDeleteMissingRegistration(){
        Registration registration = new Registration("9ADMC", "3981", "9851", "Pending");
        EM.deleteRegistration(registration, resultCapturer);
        assertTrue(resultCapturer.get().isSuccess(), "Delete should fail if the registration isn't in the database");
    }

    @Test
    void testFetchEventById(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23");
        mdb.insertEvent(event, r->{});
        EM.fetchEventById("F11", resultCapturer);
        assertEquals(event, testSession.getCurrentEvent(), "Fetched event should be in the database");
        assertTrue(resultCapturer.get().isSuccess(), "Fetch should have worked");
    }

    @Test
    void testFetchEventByIdMissingEvent(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23");
        EM.fetchEventById("F11", resultCapturer);
        assertNotEquals(event, testSession.getCurrentEvent(), "Database should have remained the same");
        assertFalse(resultCapturer.get().isSuccess(), "Fetch should have worked");
    }


    // Don't know how to test this one???
//    @Test
//    void testFetchAllRegistrations(){
//
//        Registration r_a = new Registration("9ADMD", "person2", "9851", "Declined");
//        Registration r_b = new Registration("9ADMC", "person1", "9851", "Pending");
//        mdb.insertRegistration(r_a, r->{});
//        mdb.insertRegistration(r_b, r->{});
//
//        List<Registration> regList = new ArrayList<>();
//        Consumer<List<Registration>> tempConsumer = null;
//        EM.fetchAllRegistrations("9851", tempConsumer);
//    }
    //

    @Test
    void testWaitingListCount(){
        Registration r_a = new Registration("9ADMD", "person2", "9851", "Declined");
        Registration r_b = new Registration("9ADMC", "person1", "9851", "Pending");
        Registration r_c = new Registration("9ADMC", "person3", "9851", "accpeted");
        mdb.insertRegistration(r_a, r->{});
        mdb.insertRegistration(r_b, r->{});
        mdb.insertRegistration(r_c, r->{});
        // Now what?????
    }

    @Test
    void testAddNoneActorToWaitingList(){
        EM.addUserToWaitList("9851", resultCapturer->{
            assertFalse(resultCapturer);
        });
    }

    @Test
    void testAddActorToWaitingList(){
        Actor actor = new Actor("99XAO", "bichael", "binance@gmail.com", "1239812415");
        testSession.setCurrentActor(actor);
        EM.addUserToWaitList("F11", val -> assertTrue(val));
    }
    @Test
    void testAddActorToDoubleWaitingList(){
        Actor actor = new Actor("99XAO", "bichael", "binance@gmail.com", "1239812415");
        testSession.setCurrentActor(actor);
        mdb.addUserToWaitList("F11", actor, r->{}); // insert into database
        EM.addUserToWaitList("F11", val -> assertFalse(val));
    }




}
