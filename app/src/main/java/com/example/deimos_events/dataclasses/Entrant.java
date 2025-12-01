package com.example.deimos_events.dataclasses;
import com.example.deimos_events.Roles;
/**
 * Represents an Entrant in the system.
 * <p>
 * Entrants are regular users who can register for events, join waitlists,
 * view event details, and receive notifications about their registrations.
 * This class extends {@link Actor} and automatically assigns the {@code ENTRANT} role.
 * </p>
 */
public class Entrant extends Actor {
    /**
     * Constructs a new Entrant with the specified profile information.
     *
     * @param deviceIdentifier the unique device identifier for the entrant
     * @param name the entrant's full name
     * @param email the entrant's email address
     * @param phoneNumber the entrant's phone number
     * @param notificationsPreference whether the entrant has enabled notifications
     */
    public Entrant(String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ENTRANT, notificationsPreference);
    }
}
