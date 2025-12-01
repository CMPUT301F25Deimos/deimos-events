package com.example.deimos_events;
import static org.junit.jupiter.api.Assertions.*;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.dataclasses.Result;
import com.example.deimos_events.managers.NotificationManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.ui.notifications.NotificationsArrayAdapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationManagerTest {
    private MockDatabase mdb;
    private Session testSession;
    private SessionManager testSessionManager;
    private NotificationManager NM;
    private ResultCapturer resultCapturer;

    @BeforeEach
    void init() {
        resultCapturer = new ResultCapturer();
        testSessionManager = new SessionManager(false);
        mdb = new MockDatabase();
        testSession = new Session(mdb);
        testSessionManager.setSession(testSession);
        NM = new NotificationManager(testSessionManager);
    }

    @Test
    void testInsertNotificationsSuccess() {
        NM.insertNotifications("Mikey", "Rosie", "HI", "89812", "G1212", resultCapturer);
        Result r = resultCapturer.get();
        assertTrue(r.getCond());
        assertEquals("INSERT_NOTIFICATION", r.getOperation());
        assertEquals("Notification inserted successfully", r.getMessage());
    }

    @Test
    void testFetchNotificationReceiversReturnsList() {
        List<String> recipients = Arrays.asList("Bail", "Flail");

        NM.fetchNotificationReceivers("Temple", recipients, receivers -> {
            assertNotNull(receivers);
            assertEquals(2, receivers.size());
            // We don't care about the exact map structure, just that we got 2 entries.
        });
    }

    @Test
    void testInsertNotificationsPreferenceSuccess() {
        Actor actor = new Actor("Malib333", "IKE", "IKE@example.com", "199", false);
        NM.insertNotificationsPreference(actor, true, resultCapturer);
        Result r = resultCapturer.get();
        assertTrue(r.getCond());
        assertEquals("INSERT_PREFERENCE", r.getOperation());
        assertEquals("Preference updated successfully", r.getMessage());
    }

    @Test
    void testFetchNotificationsPreferenceAfterInsert() {
        Actor actor = new Actor("Malib333", "IKE", "IKE@example.com", "199", false);
        NM.insertNotificationsPreference(actor, true, resultCapturer);
        NM.fetchNotificationsPreference(actor, pref->{
            assertTrue(pref, "Preference should be true after successful insert");
        });
    }




}
