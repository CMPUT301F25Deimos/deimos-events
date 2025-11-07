package com.example.deimos_events;

import com.google.firebase.firestore.auth.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
public class UserInterfaceManagerTest {
    private MockDatabase mdb;
    private Session testSession;
    private UserInterfaceManager UIM;

    private ResultCapturer resultCapturer;

    private Actor testActor = new Actor("912312","jack", "email@gmail.com", "1231231234", Roles.ENTRANT);

    private final Consumer<Result> NO_OP = result -> {};

    @BeforeEach
    void init(){
        resultCapturer = new ResultCapturer();
        SessionManager testSessionManager = new SessionManager(false);
        mdb = new MockDatabase();
        testSession = new Session(mdb);
        testSessionManager.setSession(testSession);
        UIM = new UserInterfaceManager(testSessionManager);
    }

    @Test
    void testSetCurrentActor(){
        UIM.setCurrentActor(testActor);
        assertEquals(UIM.getCurrentActor(), testActor);
    }
}
