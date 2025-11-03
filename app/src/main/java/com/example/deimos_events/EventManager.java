package com.example.deimos_events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManager {

    private final SessionManager sessionManager;
    private List<Event> allEventsList = new ArrayList<>(); // store events if needed

    public EventManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }

    public void joinWaitingList(Event event) {
        Session session = sessionManager.getSession();
        Actor actor = session.getCurrentActor();

        // Check if already in waiting list
        for (String id : event.waitingListParticipantIds) {
            if (id.equals(actor.getDeviceIdentifier())) {
                sessionManager.setResult(new Result(false, "You are already on the waiting list."));
                return;
            }
        }

        // Add actor to waiting list
        List<String> updatedList = new ArrayList<>(Arrays.asList(event.waitingListParticipantIds));
        updatedList.add(actor.getDeviceIdentifier());
        event.waitingListParticipantIds = updatedList.toArray(new String[0]);

        // Update in database
        Database db = session.getDatabase();
        db.updateEvent(event, success -> {
            if (success) {
                sessionManager.setResult(new Result(true, "You’re on the waiting list!"));
            } else {
                sessionManager.setResult(new Result(false, "Failed to join the waiting list."));
            }
        });
    }
    public void leaveWaitingList(Event event) {
        Session session = sessionManager.getSession();
        Actor actor = session.getCurrentActor();

        boolean wasOnList = false;
        List<String> updatedList = new ArrayList<>();

        for (String id : event.waitingListParticipantIds) {
            if (id.equals(actor.getDeviceIdentifier())) {
                wasOnList = true; // Found the actor
            } else {
                updatedList.add(id); // Keep everyone else
            }
        }

        if (!wasOnList) {
            sessionManager.setResult(new Result(false, "You are not currently on this waiting list."));
            return;
        }

        event.waitingListParticipantIds = updatedList.toArray(new String[0]);

        // Update in database
        Database db = session.getDatabase();
        db.updateEvent(event, success -> {
            if (success) {
                sessionManager.setResult(new Result(true, "You have left the waiting list."));
            } else {
                sessionManager.setResult(new Result(false, "Failed to leave the waiting list."));
            }
        });
    }


    public Event getEventById(String eventId){
        for(Event e : allEventsList){
            if(e.id.equals(eventId)) return e;
        }
        return null;
    }

}
