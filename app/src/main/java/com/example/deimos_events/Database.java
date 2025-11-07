package com.example.deimos_events;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Database implements IDatabase {
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

    public void insertActor(Actor actor, Consumer<Boolean> callback){
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

    public void insertEvent(Event event, Consumer<Boolean> callback) {
        db.collection("events")
                .document(event.id)
                .set(event)
                .addOnSuccessListener(e -> callback.accept(true))
                .addOnFailureListener(e -> callback.accept(false));
    }
    public void actorExistsByEmail(String email, Consumer<Boolean> callback) {
        db.collection("actors")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(q -> callback.accept(!q.isEmpty()))
                .addOnFailureListener(e -> callback.accept(null)); // null = error path
    }
    public void upsertActorWithRole(Actor actor, String role, java.util.function.Consumer<Boolean> callback) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("deviceIdentifier", actor.getDeviceIdentifier());
        data.put("name", actor.getName());
        data.put("email", actor.getEmail());
        data.put("phoneNumber", actor.getPhoneNumber());
        data.put("role", role);

        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .set(data) // write a flat map so Firestore always has "role"
                .addOnSuccessListener(v -> callback.accept(Boolean.TRUE))
                .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
    }

    public void deleteEntrantCascade(String entrantId, Consumer<Boolean> callback) {
        db.collection("registrations").whereEqualTo("entrantId", entrantId).get()
                .addOnSuccessListener(regSnap -> {
                    db.collection("waiting_lists").whereEqualTo("entrantId", entrantId).get()
                            .addOnSuccessListener(waitSnap -> {
                                WriteBatch batch = db.batch();

                                batch.delete(db.collection("actors").document(entrantId));

                                for (DocumentSnapshot d : regSnap.getDocuments()) {
                                    batch.delete(d.getReference());
                                }

                                for (DocumentSnapshot d : waitSnap.getDocuments()) {
                                    batch.delete(d.getReference());
                                }

                                batch.commit()
                                        .addOnSuccessListener(v -> callback.accept(Boolean.TRUE))
                                        .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
                            })
                            .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
                })
                .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
    }

    public void getAvailableEvents(Actor actor, Consumer<List<Event>> callback) {
        db.collection("registrations")
                .whereEqualTo("entrantId", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(registrationSnapshot -> {
                    Set<String> registeredEventIds = new HashSet<>();
                    for (DocumentSnapshot doc : registrationSnapshot.getDocuments()) {
                        registeredEventIds.add(doc.getString("eventId"));
                    }
                    db.collection("events")
                            .get()
                            .addOnSuccessListener(eventSnapshot -> {
                                List<Event> availableEvents = new ArrayList<>();
                                for (DocumentSnapshot doc : eventSnapshot.getDocuments()) {
                                    if (!registeredEventIds.contains(doc.getId())) {
                                        availableEvents.add(doc.toObject(Event.class));
                                    }
                                }
                                callback.accept(availableEvents);
                            })
                            .addOnFailureListener(e -> callback.accept(Collections.emptyList()));
                })
                .addOnFailureListener(e -> callback.accept(Collections.emptyList()));
    }

    public void getActorByID(String id, Consumer<Actor> callback) {
        db.collection("actors").document(id).get()
                .addOnSuccessListener(docSnapshot -> {
                    if (docSnapshot.exists()) {
                        Actor actor = docSnapshot.toObject(Actor.class);
                        callback.accept(actor);
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(exception -> {
                    callback.accept(null);
                });
    }
    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback){
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(querySnapshot->{
                    boolean alreadyExists = false;
                    for(DocumentSnapshot doc : querySnapshot.getDocuments()){
                        String entrantId = doc.getString("entrantId");
                        if (entrantId != null && entrantId.equals(actor.getDeviceIdentifier())){
                            alreadyExists = true;
                            break;
                        }
                    }
                    if(alreadyExists){
                        callback.accept(false);
                    }else{
                        //Finish implementing this
                        Map<String, Object> registrationData = new HashMap<>();
                        registrationData.put("entrantId", actor.getDeviceIdentifier());
                        registrationData.put("eventId", eventId);
                        registrationData.put("status", "Pending");

                        db.collection("registrations")
                                .add(registrationData)
                                .addOnSuccessListener(ref-> callback.accept(true))
                                .addOnFailureListener(e -> callback.accept(false));


                    }
                })
                .addOnFailureListener(e-> callback.accept(false));
    }
    public void getEventById(String eventId, Consumer<Event> callback){
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.accept(event);
                    }else{
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e->{
                    callback.accept(null);
                });
    }
}

