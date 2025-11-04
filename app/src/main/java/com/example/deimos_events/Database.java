package com.example.deimos_events;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Result r;

    public FirebaseFirestore getDb() {
        return db;
    }

    public void deleteActor(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .delete()
                .addOnSuccessListener(e ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    public void createActor(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .set(actor)
                .addOnSuccessListener(e ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }
    public void createEvent(Event event, Consumer<Boolean> callback){
        db.collection("events")
                .document(event.getId())
                .set(event)
                .addOnSuccessListener(e ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e ->{
                    callback.accept(Boolean.FALSE);
                });
    }
}
