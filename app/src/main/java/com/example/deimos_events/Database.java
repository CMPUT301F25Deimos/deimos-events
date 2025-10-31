package com.example.deimos_events;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class Database {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Result r;

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
}
