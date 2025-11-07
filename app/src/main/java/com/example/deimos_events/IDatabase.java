package com.example.deimos_events;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
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


    DocumentReference getEvent(String eventId,Consumer<Boolean> callback);


    //void fetchEventById(String eventId, Consumer<Event> callback);

    void insertEvent(Event event, Consumer<Boolean> callback);

    void updateImage(String eventId, String posterIdArray);

    void deleteRegistor(String entrantId, String eventId);


    void getRegistration(String eventId, Consumer<List<Registration>> callback);

    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback);

    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback);

    public void getEventById(String eventId, Consumer<Event> callback);





}
