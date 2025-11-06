package com.example.deimos_events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MockDatabase implements IDatabase{
    private final Map<String, Actor> mockData = new HashMap<>();


    @Override
    public void deleteActor(Actor actor, Consumer<Boolean> callback){
        mockData.remove(actor.getDeviceIdentifier());
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void insertActor(Actor actor, Consumer<Boolean> callback){
        mockData.put(actor.getDeviceIdentifier(), actor);
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void actorExists(Actor actor, Consumer<Boolean> callback){
        Boolean exists = mockData.containsKey(actor.getDeviceIdentifier());
        callback.accept(exists);
    }

    @Override
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    @Override
    public void actorExistsByEmail(String email, Consumer<Boolean> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }
    @Override
    public void upsertActorWithRole(Actor actor, String role, java.util.function.Consumer<Boolean> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }



}
