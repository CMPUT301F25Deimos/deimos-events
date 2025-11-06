package com.example.deimos_events;

public class Administrator extends Actor {
    public Administrator(String deviceIdentifier, String name, String email, String phoneNumber) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ADMIN);
    }
}
