package com.example.deimos_events;

public class Organizer extends Actor {
    public Organizer(String deviceIdentifier, String name, String email, String phoneNumber ) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ORGANIZER);
    }
}
