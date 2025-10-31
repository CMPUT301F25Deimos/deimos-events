package com.example.deimos_events;

public class Entrant extends Actor {
    Boolean receiveNotifications;

    public Entrant(String deviceIdentifier, String name, String email, String phoneNumber, Boolean receiveNotifications) {
        super(deviceIdentifier, name, email, phoneNumber);
        this.receiveNotifications = receiveNotifications;
    }


    public boolean receivesNotifications() {
        return this.receiveNotifications;
    }

}
