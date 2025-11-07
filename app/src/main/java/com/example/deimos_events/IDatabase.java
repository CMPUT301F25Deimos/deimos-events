package com.example.deimos_events;

import java.util.List;
import java.util.function.Consumer;

public interface IDatabase {
    public void deleteActor(Actor actor, Consumer<Boolean> callback);
    public void insertActor(Actor actor, Consumer<Boolean> callback);
    public void actorExists(Actor actor, Consumer<Boolean> callback);
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback);

    public void getActorByID(String id, Consumer<Actor> callback);
    public void actorExistsByEmail(String email, Consumer<Boolean> callback);

    public void upsertActor(Actor actor, Consumer<Boolean> callback);

    public void deleteEntrantCascade(String email, Consumer<Boolean> callback);

    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback);

    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback);

    public void getEventById(String eventId, Consumer<Event> callback);
}
