package com.example.deimos_events;

import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.ui.notifications.NotificationsArrayAdapter;
import android.content.Context;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Entrant;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Registration;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface IDatabase {
    public void deleteActor(Actor actor, Consumer<Boolean> callback);
    public void insertActor(Actor actor, Consumer<Boolean> callback);
    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Boolean> callback);
    public void actorExists(Actor actor, Consumer<Boolean> callback);
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback);

    public void fetchActorByID(String id, Consumer<Actor> callback);
    public void actorExistsByEmail(String email, Consumer<Boolean> callback);

    public void deleteEntrantCascade(String deviceIdentifier, Consumer<Boolean> callback);

    public void eventExists(Event event, Consumer<Boolean> callback);
    //void fetchEventById(String eventId, Consumer<Event> callback);

    void insertEvent(Event event, Consumer<Boolean> callback);

    void updateImage(String eventId, String posterIdArray,Consumer<Boolean> callback);

    public void fetchALLRegistrations(String eventId, Consumer<List<Registration>> callback);

    public void fetchAllEntrantsEnrolled(String eventId, Consumer<List<Entrant>> callback);
    public void getRegistrationsByStatus(String eventId, String status, Consumer<List<Registration>> callback);

    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback);

    public void getActorById(String deviceIdentifier, Consumer<Actor> callback);
    public void deleteRegistration(String registrationId, Consumer<Boolean> callback);
    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback);

    public void fetchEventById(String eventId, Consumer<Event> callback);
    
    public void joinEvent(Context context, String eventId, Actor actor, Consumer<Boolean> callback);
    
    public void leaveEvent(String eventId, Actor actor, Consumer<Boolean> callback);
    
    public void getEvents(Consumer<List<Event>> callback);
    
    public void getEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback);
    
    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback);
    
    public void answerEvent(String documentId, String answer, Consumer<Boolean> callback);

    public void getActorRole(Actor actor, Consumer<String> callback);
    
    public void getNotificationsPreference(Actor actor, Consumer<Boolean> callback);
    
    public void setNotificationsPreference(Actor actor, Boolean notificationsPreference, Consumer<Boolean> callback);

    public void getNotificationReceivers(String eventId, List<String> recipients, Consumer<List<Map<String, String>>> callback);
    
    public void setNotifications(String sender, String recipient, String message, String eventId, String registrationId, Consumer<Boolean> callback);

    public void getNotifications(Actor actor, ArrayList<Notifications> notificationsList, NotificationsArrayAdapter adapter);

    public void setRegistrationStatus(String registrationId, String registrationStatus, Consumer<Boolean> callback);
    //public void setRegistrationStatus(String registrationId, String registrationStatus);
    void deleteEventCascade(String eventId, java.util.function.Consumer<Boolean> callback);
    void getAllActors(java.util.function.Consumer<java.util.List<Actor>> callback);
    void deleteEventImage(String eventID, Consumer<Boolean> callback);

    public void getNotificationAdmin( Consumer<List<Notifications>> callback);




    void getNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback);
}
