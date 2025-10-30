package com.example.deimos_events;

public class Entrant extends Actor {
    Boolean receiveNotifications;

    public Entrant (String deviceIdentifier, String name, String email, String phoneNumber, Boolean receiveNotifications ) {
        super(deviceIdentifier, name, email, phoneNumber);
        this.receiveNotifications = receiveNotifications;
    }

    public void removeProfile() {
        // TODO: Remove the user's profile. Then they get redirected to the logged out activity.
    }

    /**
     * Join a waiting list for an event
     * @param event The event to join
     */
    public void joinWaitingListForEvent(Event event) {
        // TODO: join the waiting list
    }

    /**
     * Leave a waiting list for an event
     * @param event The event to leave
     */
    public void leaveWaitingListForEvent(Event event) {
        // TODO: leave the waiting list
    }

    /**
     * Get the events the user can join the waitlist of
     * @return The joinable events
     */
    public Event[] getWaitingListsToJoin() {
        Event[] events = {};
        // TODO: query the DB (make sure they have slots)
        return events;
    }
}
