package com.example.deimos_events;

/**
 * An abstract class for all types of actors to inherit properties and methods from.
 */
public abstract class Actor {
    String deviceIdentifier;
    String name;
    String email;
    String phoneNumber;
    public Actor (String deviceIdentifier, String name, String email, String phoneNumber ) {
        this.deviceIdentifier = deviceIdentifier;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void updateProfile(String name, String email, String phoneNumber) {
        // TODO: update in database
    }
}
