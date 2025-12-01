package com.example.deimos_events;

/**
 * Class for entrants. This also contains methods for getting data related to registrations and events.
 */
public class Entrant extends Actor {

    public Entrant(String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ENTRANT, notificationsPreference);
    }
    
}
