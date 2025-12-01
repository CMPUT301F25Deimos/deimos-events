package com.example.deimos_events;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.deimos_events.dataclasses.Actor;

import org.junit.jupiter.api.BeforeEach;

public class ActorTest {
    private Actor testActor;
    @BeforeEach
    void init(){
       testActor = new Actor("Zero", "John", "johnsemail@gmail.com", "778-112-9821", false);
    }

    @Test
    void testGetName(){
        assertEquals("John", testActor.getName());
    }

    @Test
    void testGetDeviceID(){
        assertEquals("Zero", testActor.getDeviceIdentifier());
    }

    @Test
    void testGetEmail(){
        assertEquals("johnsemail@gmail.com", testActor.getEmail());
    }

    @Test
    void testGetPhone(){
        assertEquals("778-112-9821", testActor.getPhoneNumber());
    }
}
