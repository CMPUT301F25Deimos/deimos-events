package com.example.deimos_events;

/**
 * Class for entrants. This also contains methods for getting data related to registrations and events.
 */
public class Entrant extends Actor {
    Boolean receiveNotifications;

    public Entrant() {}
    public Entrant(String deviceIdentifier, String name, String email, String phoneNumber, Boolean receiveNotifications) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ENTRANT);
        this.receiveNotifications = receiveNotifications;
    }

    public boolean getReceiveNotifications() {
        return this.receiveNotifications;
    }
}
