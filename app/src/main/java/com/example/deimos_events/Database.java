package com.example.deimos_events;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Database implements IDatabase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insertActor(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                //.document(actor.getDeviceIdentifier())
                .set(actor)
                .addOnSuccessListener(tempVoid ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Boolean> callback){

        db.collection("actors")
                .document(oldActor.getDeviceIdentifier())
                .update(
                        "name", updatedActor.getName(),
                        "email", updatedActor.getEmail(),
                        "phoneNumber", updatedActor.getPhoneNumber(),
                        "role", updatedActor.getRole())
                .addOnSuccessListener(tempVoid->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e->{
                    callback.accept(Boolean.FALSE);
                });

    }

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
    public void createEvent(Event event, Consumer<Boolean> callback){
        db.collection("events")
                .document()
                .set(event)
                .addOnSuccessListener(e ->{
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e ->{
                    callback.accept(Boolean.FALSE);
                });
    }

    @Override
    public void updateImage(String eventId, String posterIdArray) {
        db.collection("events")
                .document(eventId)
                .update("posterIdArray", posterIdArray);
    }

    @Override
    public void deleteRegistor(String entrantId, String eventId) {
         db.collection("registrations")
                .whereEqualTo("entrantId", entrantId)
                .whereEqualTo("eventId", eventId)
                .get().addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
;
    }

    @Override
    public void getRegistration(String eventId, Consumer<List<Registration>> callback) {
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Registration registration = doc.toObject(Registration.class);
                        registrations.add(registration);
                    }
                    callback.accept(registrations);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error getting registrations: " + e.getMessage());
                    callback.accept(Collections.emptyList());
                });
    }



    public void deleteEntrantCascade(String deviceIdentifier, Consumer<Boolean> callback) {
        db.collection("registrations").whereEqualTo("deviceIdentifier", deviceIdentifier).get()
                .addOnSuccessListener(regSnap -> {
                    db.collection("waiting_lists").whereEqualTo("deviceIdentifier", deviceIdentifier).get()
                            .addOnSuccessListener(waitSnap -> {
                                WriteBatch batch = db.batch();

                                batch.delete(db.collection("actors").document(deviceIdentifier));

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

    public void actorExists(Actor actor, Consumer<Boolean> callback){
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                //.document(actor.getDeviceIdentifier())
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


    @Override
    public DocumentReference getEvent(String eventId, Consumer<Boolean> callback) {
        return null;
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

    public void fetchActorByID(String id, Consumer<Actor> callback) {
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
}
