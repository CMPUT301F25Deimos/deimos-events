package com.example.deimos_events;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void deleteActor(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .delete()
                .addOnSuccessListener(tempVoid ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    public void upsertActor(Actor actor, Consumer<Boolean> callback){
        // temp for testing
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .set(actor)
                .addOnSuccessListener(tempVoid ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    public void actorExists(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(doc ->{
                    if (doc.exists()){
                       callback.accept(Boolean.TRUE);
                    }else{
                        callback.accept(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.accept(null);
                });
    }

    public void upsertEvent(Event event, Consumer<Boolean> callback) {
        db.collection("events")
                .document(event.id)
                .set(event)
                .addOnSuccessListener(e -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }
}
