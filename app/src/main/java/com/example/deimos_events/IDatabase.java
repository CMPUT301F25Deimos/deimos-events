package com.example.deimos_events;

import android.content.Context;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
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

    void deleteRegistor(String id, Consumer<Boolean> callback);


    public void fetchALLRegistrations(String eventId, Consumer<List<Registration>> callback);

    public void fetchAllEntrantsEnrolled(String eventId, Consumer<List<Entrant>> callback);
    public void fetchEventAttendees(String eventId, Consumer<List<Entrant>> callback);
    public void fetchWaitlistEntrants(String eventId, Consumer<List<Entrant>> callback);

    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback);

    public void registrationExists(String Id, Consumer<Boolean> callback);
    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback);

    public void fetchEventById(String eventId, Consumer<Event> callback);
    
    public void joinEvent(Context context, String eventId, Actor actor);
    
    public void leaveEvent(String eventId, Actor actor);
    
    public void getEvents(Consumer<List<Event>> callback);
    
    public void getEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback);
    
    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback);
    
    public void getNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback);
    
    public void answerEvent(String documentId, String answer);
    
    public void getActorRole(Actor actor, Consumer<String> callback);

    void deleteEventCascade(String eventId, java.util.function.Consumer<Boolean> callback);
    void getAllActors(java.util.function.Consumer<java.util.List<Actor>> callback);



}
