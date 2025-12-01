package com.example.deimos_events.dataclasses;

import com.example.deimos_events.Roles;

/**
 * Represents an Administrator actor in the system.
 * <p>
 * Administrators have elevated permissions and can manage organizers,
 * events, and other system-level operations. This class extends the
 * {@link Actor} class and automatically assigns the {@code ADMIN} role.
 * </p>
 */
public class Administrator extends Actor {

    /**
     * Creates a new Administrator with the specified properties.
     *
     * @param deviceIdentifier the unique device identifier for the administrator
     * @param name the administrator's name
     * @param email the administrator's email address
     * @param phoneNumber the administrator's phone number
     * @param notificationsPreference whether the administrator has notifications enabled
     */
    public Administrator(String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ADMIN, notificationsPreference);
    }
}
