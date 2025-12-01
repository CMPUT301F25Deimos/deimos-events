package com.example.deimos_events;

import android.content.Context;

import com.example.deimos_events.dataclasses.Actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.example.deimos_events.dataclasses.Entrant;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.ui.notifications.NotificationsAdminArrayAdapter;
import com.example.deimos_events.ui.notifications.NotificationsArrayAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class MockDatabase implements IDatabase {
    private final Map<String, Actor> mockActors = new HashMap<>();
    private final Map<String, Event> mockEvents = new HashMap<>();
    private final Map<String, Registration> mockRegistrations = new HashMap<>();
    private final Map<String, Boolean> notificationsPreference = new HashMap<>();

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
        callback.accept(new ArrayList<>(mockEvents.values()));
    }


    @Override
    public void getWaitingRegistrationsForEvent(String eventId, Consumer<Integer> callback) {
        int count = 0;
        if (eventId != null) {
            for (Registration r : mockRegistrations.values()) {
                if (eventId.equals(r.getEventId()) && "Waiting".equals(r.getStatus())) {
                    count = count + 1;
                }
            }
        }
        callback.accept(count);
    }


    @Override
    public void deleteRegistration(String registrationId, Consumer<Boolean> callback) {
        Object res = mockRegistrations.remove(registrationId);
        if (res == null){
            callback.accept(Boolean.FALSE);
        } else {
            callback.accept(Boolean.TRUE);
        }
    }

    @Override
    public void actorExistsByEmail(String email, Consumer<Boolean> callback) {

        boolean found = false;
        try {
            for (Actor a : mockActors.values()) {
                if (a.getEmail().equals(email)) {
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
        Registration registration = new Registration(id, actor.getDeviceIdentifier(), eventId, "Pending", "38.8951", "77.0364");
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

    private final Map<String, List<Entrant>> mockEntrantsByEvent = new HashMap<>();

    public void insertEntrantForEvent(String eventId, Entrant entrant) {
        mockEntrantsByEvent
                .computeIfAbsent(eventId, k -> new ArrayList<>())
                .add(entrant);
    }

    @Override
    public void fetchAllEntrantsEnrolled(String eventId, Consumer<List<Entrant>> callback) {
        List<Entrant> list = mockEntrantsByEvent.getOrDefault(eventId, Collections.emptyList());
        // Return a copy to avoid external modification
        callback.accept(new ArrayList<>(list));
    }


    @Override
    public void getRegistrationsByStatus(String eventId, String status, Consumer<List<Registration>> callback) {
        List<Registration> result = new ArrayList<>();
        for (Registration r : mockRegistrations.values()) {
            if (eventId.equals(r.getEventId()) && status.equals(r.getStatus())) {
                result.add(r);
            }
        }
        callback.accept(result);
    }



    @Override
    public void updateImage(String eventId, String posterIdArray, Consumer<Boolean> callback) {
        if (eventId == null || posterIdArray == null) {
            callback.accept(Boolean.FALSE);
            return;
        }
        boolean exists = mockEvents.containsKey(eventId);
        callback.accept(exists);
    }



    @Override
    public void joinEvent(Context context, String eventId, Actor actor, Consumer<Boolean> callback) {
        if (actor == null || eventId == null || !mockEvents.containsKey(eventId)) {
            callback.accept(Boolean.FALSE);
            return;
        }

        // If registration alred xes, just report success
        for (Registration r : mockRegistrations.values()) {
            if (eventId.equals(r.getEventId()) &&
                    actor.getDeviceIdentifier().equals(r.getEntrantId())) {
                callback.accept(Boolean.TRUE);
                return;
            }
        }

        String id = UUID.randomUUID().toString();
        Registration registration = new Registration(id, actor.getDeviceIdentifier(), eventId, "Joined", "0", "0"
        );
        mockRegistrations.put(id, registration);
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void leaveEvent(String eventId, Actor actor, Consumer<Boolean> callback) {
        if (actor == null || eventId == null) {callback.accept(Boolean.FALSE);
            return;
        }

        boolean removedAny = mockRegistrations.values().removeIf(r ->
                eventId.equals(r.getEventId()) &&
                        actor.getDeviceIdentifier().equals(r.getEntrantId())
        );
        callback.accept(removedAny);
    }



    @Override
    public void getEvents(Consumer<List<Event>> callback) {
        callback.accept(new ArrayList<>(mockEvents.values()));
    }


    @Override
    public void getEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback) {
        if (actor == null) {
            callback.accept(Collections.emptySet());
            return;
        }
        Set<String> eventIds = new HashSet<>();
        for (Registration r : mockRegistrations.values()) {
            if (actor.getDeviceIdentifier().equals(r.getEntrantId())) {
                eventIds.add(r.getEventId());
            }
        }
        callback.accept(eventIds);
    }



    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }


    @Override
    public void getNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback) {
        if (actor == null) {
            callback.accept(Collections.emptyList());
            return;
        }

        List<Registration> res = new ArrayList<>();
        for (Registration r : mockRegistrations.values()) {
            if (actor.getDeviceIdentifier().equals(r.getEntrantId())) {
                res.add(r);
            }
        }
        callback.accept(res);
    }


    @Override
    public void answerEvent(String documentId, String answer, Consumer<Boolean> callback){
        boolean exists = mockRegistrations.containsKey(documentId);
        callback.accept(exists);
    }

    @Override
    public void getActorRole(Actor actor, Consumer<String> callback) {
        if (actor == null) {
            callback.accept(null);
        } else {
            callback.accept("Entrant");
        }
    }


    @Override
    public void getNotificationsPreference(Actor actor, Consumer<Boolean> callback) {
        if (actor == null) {callback.accept(Boolean.FALSE);
            return;
        }
        Boolean pref = notificationsPreference.get(actor.getDeviceIdentifier());
        callback.accept(pref != null ? pref : Boolean.FALSE);
    }


    @Override
    public void setNotificationsPreference(Actor actor, Boolean notificationsPreferenceValue, Consumer<Boolean> callback) {
        if (actor == null || notificationsPreferenceValue == null) {
            callback.accept(Boolean.FALSE);
            return;
        }
        notificationsPreference.put(actor.getDeviceIdentifier(), notificationsPreferenceValue);
        callback.accept(Boolean.TRUE);
    }


    @Override
    public void getNotificationReceivers(String eventId, List<String> recipients, Consumer<List<Map<String, String>>> callback) {
        if (eventId == null) {callback.accept(Collections.emptyList());
            return;
        }

        List<Map<String, String>> result = new ArrayList<>();
        if (recipients != null) {
            for (String r : recipients) {
                Map<String, String> map = new HashMap<>();
                map.put("id", r);
                result.add(map);
            }
        }
        callback.accept(result);
    }


    @Override
    public void setNotifications(String sender, String recipient, String message, String eventId, String registrationId, Consumer<Boolean> callback) {
        if (sender == null || recipient == null || message == null || eventId == null || registrationId == null) {
            callback.accept(Boolean.FALSE);
        } else {
            callback.accept(Boolean.TRUE);
        }
    }

    @Override
    public void setRegistrationStatus(String registrationId, String registrationStatus, Consumer<Boolean> callback) {
        Registration r = mockRegistrations.get(registrationId);
        if (r == null) {
            callback.accept(Boolean.FALSE);
            return;
        }
        r.setStatus(registrationStatus);
        callback.accept(Boolean.TRUE);
    }
    @Override
    public void deleteEventCascade(String eventId, Consumer<Boolean> callback) {
        if (eventId == null) {
            callback.accept(Boolean.FALSE);
            return;
        }
        boolean removed = mockEvents.remove(eventId) != null;
        mockRegistrations.values().removeIf(r -> eventId.equals(r.getEventId()));
        callback.accept(removed);
    }


    @Override
    public void getAllActors(Consumer<List<Actor>> callback) {
        callback.accept(new ArrayList<>(mockActors.values()));
    }


    @Override
    public void deleteEventImage(String eventID, Consumer<Boolean> callback) {
        boolean exists = mockEvents.containsKey(eventID);
        callback.accept(exists);
    }

    @Override
    public void getNotifications(Actor actor, ArrayList<Notifications> notificationsList, NotificationsArrayAdapter adapter) {
        // In this mock, we don't actually fetch anything; just notify adapter.
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getNotificationAdmin(NotificationsAdminArrayAdapter adapter,
                                     ArrayList<Notifications> notificationsList) {
        // Same idea for admin notifications in the mock.
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


}


