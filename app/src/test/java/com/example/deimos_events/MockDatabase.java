package com.example.deimos_events;

import android.content.Context;

import com.example.deimos_events.dataclasses.Actor;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Registration;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class MockDatabase implements IDatabase {
    private final Map<String, Actor> mockActors = new HashMap<>();
    private final Map<String, Event> mockEvents = new HashMap<>();
    private final Map<String, Registration> mockRegistrations = new HashMap<>();

    private String regKey(String ActorKey, String EventKey) {
        return ActorKey + "_" + EventKey;
    }


    @Override
    public void deleteActor(Actor actor, Consumer<Boolean> callback) {
        mockActors.remove(actor.getDeviceIdentifier());
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void insertActor(Actor actor, Consumer<Boolean> callback) {
        mockActors.put(actor.getDeviceIdentifier(), actor);
        callback.accept(Boolean.TRUE);
    }

    public void insertEvent(Event event, Consumer<Boolean> callback) {

        if (event == null){
            callback.accept(Boolean.FALSE);
            return;
        }

        if (mockEvents.containsKey(event.getId())){
            callback.accept(Boolean.FALSE);
            return;
        }
        mockEvents.put(event.getId(), event);
        callback.accept(Boolean.TRUE);
    }

    public void eventExists(Event event, Consumer<Boolean> callback) {
        callback.accept((mockEvents.containsKey(event.getId())));
    }

    public void insertRegistration(Registration registration, Consumer<Boolean> callback) {
        try {
            //String registrationID = regKey(registration.getEntrantId(), registration.getEventId());
           mockRegistrations.put(registration.getId(), registration);
           callback.accept(Boolean.TRUE);
        } catch (Exception e) {
            callback.accept(Boolean.FALSE);
        }
    }

    @Override
    public void actorExists(Actor actor, Consumer<Boolean> callback) {
        Boolean exists = mockActors.containsKey(actor.getDeviceIdentifier());
        callback.accept(exists);
    }

    @Override
    public void getAvailableEvents(Actor actor, Consumer<List<Event>> callback) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    @Override
    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    @Override
    public void actorExistsByEmail(String email, Consumer<Boolean> callback) {

        boolean found = false;
        try {
            for (Actor a : mockActors.values()) {
                if (a.getEmail() == email) {
                    found = true;
                    callback.accept(found);
                    break;
                }
            }
            callback.accept(found);
        } catch (Exception e) {
            callback.accept(null);
        }
    }

    @Override
    public void fetchActorByID(String id, Consumer<Actor> callback) {
        callback.accept(mockActors.get(id));
    }

    @Override
    public void deleteEntrantCascade(String deviceIdentifier, Consumer<Boolean> callback) {
        try {

            mockActors.remove(deviceIdentifier); // remove the actor
            mockRegistrations.values().removeIf(r ->
                    deviceIdentifier.equals(r.getEntrantId())
            );

            callback.accept(Boolean.TRUE);
        } catch (Exception e) {
            callback.accept(Boolean.FALSE);
        }
    }

    @Override
    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Boolean> callback) {
        if (mockActors.containsKey(oldActor.getDeviceIdentifier())) {
            mockActors.put(oldActor.getDeviceIdentifier(), updatedActor);
            callback.accept(Boolean.TRUE);
        } else {
            callback.accept(Boolean.FALSE);
        }
    }

    @Override
    public void fetchEventById(String eventId, Consumer<Event> callback) {
        callback.accept(mockEvents.get(eventId));
    }

    @Override
    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback) {
        if (actor == null || eventId == null) {
            callback.accept(Boolean.FALSE);
            return; // both need to exist
        }

        boolean exists = Boolean.FALSE;
        for (Registration r : mockRegistrations.values()) {
            if (r.getEventId().equals(eventId) && r.getEntrantId().equals(actor.getDeviceIdentifier())
                    && "Pending".equals(r.getStatus())) {
                exists = Boolean.TRUE;
                break;
            }
        }
        if (exists) {
            callback.accept(false); // found one already, don't need another registration
            return;
        }

        String id = UUID.randomUUID().toString();
        Registration registration = new Registration(id, actor.getDeviceIdentifier(), eventId, "Pending");
        mockRegistrations.put(id, registration);
        callback.accept(true);

    }


    @Override
    public void fetchALLRegistrations(String eventId, Consumer<List<Registration>> callback) {
        List<Registration> regList = new ArrayList<>();
        for (Registration r : mockRegistrations.values()) {
            if (r.getEventId().equals(eventId)) {
                regList.add(r);
            }
        }
        callback.accept(regList);
    }

    @Override
    public void registrationExists(String Id, Consumer<Boolean> callback) {
        callback.accept(mockRegistrations.containsKey(Id));
    }

    @Override
    public void deleteRegistor(String Id, Consumer<Boolean> callback) {
        if (mockRegistrations.remove(Id) != null) {
            callback.accept(Boolean.TRUE);
        } else {
            callback.accept(false);
        }
    }

    @Override
    public void updateImage(String eventId, String posterIdArray, Consumer<Boolean> callback) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }


    @Override
    public void joinEvent(Context context, String eventId, Actor actor) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    public void leaveEvent(String eventId, Actor actor) {
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    public void getEvents(Consumer<List<Event>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    public void getEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }


    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    public void getNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    public void answerEvent(String documentId, String answer){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    @Override
    public void getActorRole(Actor actor, Consumer<String> callback) {
        throw new UnsupportedOperationException("Not Implemented yet");

    }


}

//    @Override
//    public DocumentReference getEvent(String eventId,Consumer<Boolean> callback){
//        throw new UnsupportedOperationException("Not Implemented yet");
//    }
