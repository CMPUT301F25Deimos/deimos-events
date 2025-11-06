package com.example.deimos_events;

import java.util.List;
import java.util.function.Consumer;

public interface IDatabase {
    public void deleteActor(Actor actor, Consumer<Boolean> callback);
    public void insertActor(Actor actor, Consumer<Boolean> callback);
    public void actorExists(Actor actor, Consumer<Boolean> callback);
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback);

    public void actorExistsByEmail(String email, Consumer<Boolean> callback);

    public void upsertActorWithRole(Actor actor, String role, java.util.function.Consumer<Boolean> callback);
}
