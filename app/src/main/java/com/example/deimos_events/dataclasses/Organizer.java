package com.example.deimos_events.dataclasses;

import com.example.deimos_events.Roles;

public class Organizer extends Actor {
    public Organizer(String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        super(deviceIdentifier, name, email, phoneNumber, Roles.ORGANIZER, notificationsPreference);
    }
}
