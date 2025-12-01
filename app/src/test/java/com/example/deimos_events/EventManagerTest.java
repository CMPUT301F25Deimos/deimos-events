package com.example.deimos_events;

import static org.junit.jupiter.api.Assertions.*;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Entrant;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Registration;
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
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23", "J333", "crit", "guid");
        EM.insertEvent(event, resultCapturer);
        assertTrue(resultCapturer.get().getCond(), "Event should be in the database");
        assertEquals(event, testSession.getCurrentEvent());
    }

    @Test
    void testDoubleInsertEventInDatabase(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23", "J333", "crit", "guid");
        mdb.insertEvent(event, r->{}); // insert once
        EM.insertEvent(event, resultCapturer); // insert twice
        assertFalse(resultCapturer.get().isSuccess(), "Inserts Should fail if event is already in database");
    }

    @Test
    void testDeleteRegistration(){
        Registration registration = new Registration("9ADMC", "3981", "9851", "Pending","38.8951", "77.0364");
        mdb.insertRegistration(registration, r->{}); //
        EM.deleteRegistration(registration.getId(), resultCapturer);
        assertTrue(resultCapturer.get().isSuccess(), "Delete should succeed on deleting existent registrations");
    }
    @Test
    void testDeleteMissingRegistration(){
        Registration registration = new Registration("9ADMC", "3981", "9851", "Pending","38.8951", "77.0364");
        EM.deleteRegistration(registration.getId(), resultCapturer);
        assertFalse(resultCapturer.get().isSuccess(), "Delete should fail if the registration isn't in the database");
    }

    @Test
    void testFetchEventById(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23", "J333", "stuff", "more stuff");
        mdb.insertEvent(event, r->{});
        EM.fetchEventById("F11", resultCapturer);
        assertEquals(event, testSession.getCurrentEvent(), "Fetched event should be in the database");
        assertTrue(resultCapturer.get().isSuccess(), "Fetch should have worked");
    }

    @Test
    void testFetchEventByIdMissingEvent(){
        Event event = new Event("F11", "badminton", "img1223", "game where you hit birdy", "yesterday", 10, Boolean.FALSE, "qe23", "J333", "stuff", "more stuff");
        EM.fetchEventById("F11", resultCapturer);
        assertNotEquals(event, testSession.getCurrentEvent(), "Database should have remained the same");
        assertFalse(resultCapturer.get().isSuccess(), "Fetch should have worked");
    }


    @Test
    void testFetchAllRegistrationsReturnsList() {
        Registration r_a = new Registration("R1", "person1", "EVT", "Pending", "1", "1");
        Registration r_b = new Registration("R2", "person2", "EVT", "Accepted", "2", "2");
        Registration r_other = new Registration("R3", "otherEntrant", "OTHER", "Pending", "3", "3");

        mdb.insertRegistration(r_a, r -> {});
        mdb.insertRegistration(r_b, r -> {});
        mdb.insertRegistration(r_other, r -> {});

        EM.fetchAllRegistrations("EVT", regList -> {
            assertNotNull(regList, "List should not be null when DB succeeds");
            assertEquals(2, regList.size(), "Should return only registrations for requested event");
            assertTrue(regList.contains(r_a));
            assertTrue(regList.contains(r_b));
        });
    }

    @Test
    void testSetRegistrationStatusMissingId() {
        EM.setRegistrationStatus(null, "Accepted", result -> {
            assertFalse(result.getCond(), "Should fail when registrationId is null");
            assertEquals("UPDATE_REG_STATUS", result.getOperation());
        });
    }

    @Test
    void testSetRegistrationStatusMissingStatus() {
        EM.setRegistrationStatus("R1", null, result -> {
            assertFalse(result.getCond(), "Should fail when status is null");
            assertEquals("UPDATE_REG_STATUS", result.getOperation());
        });
    }

    @Test
    void testSetRegistrationStatusSuccess() {
        Registration r = new Registration("R1", "e1", "EVT", "Pending", "1", "1");
        mdb.insertRegistration(r, x -> {});

        EM.setRegistrationStatus("R1", "Accepted", result -> {
            assertTrue(result.getCond(), "Should succeed when registration exists");
            assertEquals("UPDATE_REG_STATUS", result.getOperation());
            assertEquals("Registration status update Success", result.getMessage());
        });
    }

    @Test
    void testSetRegistrationStatusFailsWhenMissingRegistration() {
        EM.setRegistrationStatus("MISSING", "Accepted", result -> {
            assertFalse(result.getCond(), "Should fail when registration does not exist");
            assertEquals("UPDATE_REG_STATUS", result.getOperation());
            assertEquals("Failed to update registration", result.getMessage());
        });
    }


    @Test
    void testAnswerEventMissingId() {
        EM.answerEvent(null, "yes", result -> {
            assertFalse(result.getCond(), "Should fail when registrationId is null");
            assertEquals("ANSWER_EVENT", result.getOperation());
        });
    }

    @Test
    void testAnswerEventMissingAnswer() {
        EM.answerEvent("R1", "   ", result -> {
            assertFalse(result.getCond(), "Should fail when answer is blank");
            assertEquals("ANSWER_EVENT", result.getOperation());
        });
    }

    @Test
    void testAnswerEventSuccessTrue() {
        // registration exists => MockDatabase.answerEvent returns true
        Registration r = new Registration("R1", "e1", "EVT", "Pending", "1", "1");
        mdb.insertRegistration(r, x -> {});

        EM.answerEvent("R1", "Accepted", result -> {
            assertTrue(result.getCond(), "Should be treated as success when db returns true");
            assertEquals("ANSWER_EVENT", result.getOperation());
            assertEquals("Answer saved successfully", result.getMessage());
        });
    }

    @Test
    void testWaitingListCount() {
        Registration r_a = new Registration("9ADMD", "person2", "9851", "Declined", "38.8951", "77.0364");
        Registration r_b = new Registration("9ADMC", "person1", "9851", "Waiting", "12.8951", "65.0364");
        Registration r_c = new Registration("9ADME", "person3", "9851", "Accepted", "55.8951", "77.0364");
        mdb.insertRegistration(r_a, r -> {});
        mdb.insertRegistration(r_b, r -> {});
        mdb.insertRegistration(r_c, r -> {});
        EM.getWaitingListCount("9851", resultCapturer->{
            assertEquals(1, resultCapturer);
        });
    }


    @Test
    void testAddNoneActorToWaitingList(){
        EM.addUserToWaitList("9851", resultCapturer->{
            assertFalse(resultCapturer);
        });
    }

    @Test
    void testFetchNotificationEventInfoNullActor() {
        EM.fetchNotificationEventInfo(null, regs -> {
            assertNotNull(regs);
            assertTrue(regs.isEmpty(), "Null actor should produce empty list");
        });
    }

    @Test
    void testFetchEntrantRegisteredEventsNullActor() {
        EM.fetchEntrantRegisteredEvents(null, eventIds -> {
            assertNotNull(eventIds);
            assertTrue(eventIds.isEmpty(), "Null actor should produce empty set");
        });
    }

    @Test
    void testLeaveEventMissingArgs() {
        Actor actor = null;
        EM.leaveEvent(null, actor, result -> {
            assertFalse(result.getCond(), "Should fail when actor or eventId is missing");
            assertEquals("LEAVE_EVENT", result.getOperation());
        });
    }


    @Test
    void testJoinEventMissingArgs() {
        Actor actor = null;
        EM.joinEvent(null, null, actor, result -> {
            assertFalse(result.getCond(), "Should fail when actor or eventId is missing");
            assertEquals("JOIN_EVENT", result.getOperation());
        });
    }

    @Test
    void testGetAllEvents() {
        Event e1 = new Event("E1", "event1", "img1", "desc1", "today", 5, false, "qr1", "ORG1", "stuff", "More Stuff");
        Event e2 = new Event("E2", "event2", "img2", "desc2", "tomorrow", 10, true, "qr2", "ORG2", "stuff", "more sutff");
        mdb.insertEvent(e1, r -> {});
        mdb.insertEvent(e2, r -> {});

        EM.getAllEvents(events -> {
            assertNotNull(events);
            assertTrue(events.contains(e1));
            assertTrue(events.contains(e2));
        });
    }



    @Test
    void testAddActorToWaitingList(){
        Actor actor = new Actor("99XAO", "bichael", "binance@gmail.com", "1239812415", Boolean.TRUE);
        testSession.setCurrentActor(actor);
        EM.addUserToWaitList("F11", val -> assertTrue(val));
    }
    @Test
    void testAddActorToDoubleWaitingList(){
        Actor actor = new Actor("99XAO", "bichael", "binance@gmail.com", "1239812415", Boolean.TRUE);
        testSession.setCurrentActor(actor);
        mdb.addUserToWaitList("F11", actor, r->{}); // insert into database
        EM.addUserToWaitList("F11", val -> assertFalse(val));
    }





    @Test
    void testExportEntrantsCsv() {
        Entrant e1  = new Entrant("99XAO", "bichael", "binance@gmail.com", "1239812415", Boolean.TRUE);
        Entrant e2  = new Entrant("99XAPP", "TRIichael", "Triinance@gmail.com", "1239812498", Boolean.TRUE);
        mdb.insertEntrantForEvent("EVT", e1);
        mdb.insertEntrantForEvent("EVT", e2);

        EM.exportEntrantsCsv("EVT", csv -> {
            assertNotNull(csv, "CSV should not be null");

            // header
            assertTrue(csv.startsWith("Name,Email,PhoneNo\n"));

            // first entrant row
            assertTrue(csv.contains("bichael,binance@gmail.com,1239812415"),
                    "CSV should contain first entrant row");

            // second entrant row
            assertTrue(csv.contains("TRIichael,Triinance@gmail.com,1239812498"),
                    "CSV should contain second entrant row");
        });
    }




















}
