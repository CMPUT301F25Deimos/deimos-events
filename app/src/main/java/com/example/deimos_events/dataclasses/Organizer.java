package com.example.deimos_events.dataclasses;

import com.example.deimos_events.Roles;

/**
 * Represents an organizer in the system.
 * Organizers can create events, manage participants, and perform other event-related actions.
 * This class extends {@link Actor} and automatically assigns the ORGANIZER role.
 */
public class Organizer extends Actor {

    /**
     * Creates a new Organizer with the specified information.
     * @param deviceIdentifier the device identifier of the organizer
     * @param name the organizer's name
     * @param email the organizer's email address
     * @param phoneNumber the organizer's phone number
     * @param notificationsPreference the organizer's notification preference
     */
    public Organizer(String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ORGANIZER, notificationsPreference);
    }
}
