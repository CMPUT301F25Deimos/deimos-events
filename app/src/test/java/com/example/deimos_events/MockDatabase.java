package com.example.deimos_events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MockDatabase implements IDatabase{
    private final Map<String, Actor> mockActors = new HashMap<>();
    private final Map<String, Event> mockEvents = new HashMap<>();
    private final Map<String, Registration> mockRegistrations = new HashMap<>();



    @Override
    public void deleteActor(Actor actor, Consumer<Boolean> callback){
        mockActors.remove(actor.getDeviceIdentifier());
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void insertActor(Actor actor, Consumer<Boolean> callback){
        mockActors.put(actor.getDeviceIdentifier(), actor);
        callback.accept(Boolean.TRUE);
    }

    @Override
    public void actorExists(Actor actor, Consumer<Boolean> callback){
        Boolean exists = mockActors.containsKey(actor.getDeviceIdentifier());
        callback.accept(exists);
    }

    @Override
    public void getAvailableEvents(Actor actor,Consumer<List<Event>> callback){
        throw new UnsupportedOperationException("Not Implemented yet");
    }

    @Override
    public void actorExistsByEmail(String email, Consumer<Boolean> callback){

        boolean found = false;
        try {
            for (Actor a : mockActors.values()){
                if (a.getEmail() == email){
                    found = true;
                    callback.accept(found);
                    break;
                }
            }
            callback.accept(found);
        } catch (Exception e){
            callback.accept(null);
        }
    }
    @Override
    public void fetchActorByID(String id, Consumer<Actor> callback){
        callback.accept(mockActors.get(id));
    }

    @Override
    public void deleteEntrantCascade(String deviceIdentifier, Consumer<Boolean> callback){
        try {

            mockActors.remove(deviceIdentifier); // remove the actor

            mockRegistrations.values().removeIf(r ->
                deviceIdentifier.equals(r.getEntrantId())
            );

            callback.accept(Boolean.TRUE);

        } catch (Exception e){
            callback.accept(Boolean.FALSE);
        }
    }

    @Override
    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Boolean> callback){
        if (mockActors.containsKey(oldActor.getDeviceIdentifier())){
            mockActors.put(oldActor.getDeviceIdentifier(), updatedActor);
            callback.accept(Boolean.TRUE);
        } else {
            callback.accept(Boolean.FALSE);
        }
    }


}
