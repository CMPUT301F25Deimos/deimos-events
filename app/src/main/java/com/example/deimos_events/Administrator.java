package com.example.deimos_events;

public class Administrator extends Actor {
    public Administrator(String deviceIdentifier, String name, String email, String phoneNumber ) {
        super(deviceIdentifier, name, email, phoneNumber);
    }
    public void removeProfile(String deviceIdentifier) {
        // TODO: Remove the person's profile.
    }

    public void removeEvent(String eventId) {
        // TODO: Remove the event.
    }

    public void removeImage(String imageId) {
        // TODO: Remove the image
    }
}
