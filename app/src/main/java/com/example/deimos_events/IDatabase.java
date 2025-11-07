package com.example.deimos_events;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.function.Consumer;

public interface IDatabase {
    public void deleteActor(Actor actor, Consumer<Boolean> callback);
    public void insertActor(Actor actor, Consumer<Boolean> callback);
    public void actorExists(Actor actor, Consumer<Boolean> callback);
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback);

    public void getActorByID(String id, Consumer<Actor> callback);
    public void actorExistsByEmail(String email, Consumer<Boolean> callback);

    public void upsertActorWithRole(Actor actor, String role, java.util.function.Consumer<Boolean> callback);

    DocumentReference getEvent(String eventId,Consumer<Boolean> callback);

    void createEvent(Event event, Consumer<Boolean> callback);

    void updateImage(String eventId, String posterIdArray);

    void deleteRegistor(String entrantId, String eventId);

    void getActorById(String id, Consumer<Actor> callback);

    void getRegistration(String eventId, Consumer<List<Registration>> callback);
    public void upsertActor(Actor actor, Consumer<Boolean> callback);

    public void deleteEntrantCascade(String email, Consumer<Boolean> callback);
}
