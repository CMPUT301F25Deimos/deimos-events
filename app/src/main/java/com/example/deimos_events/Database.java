package com.example.deimos_events;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public void insertEvent(Event event, Consumer<Boolean> callback){
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
    public void deleteRegistor(String id, Consumer<Boolean> callback) {
         db.collection("registrations")
                .whereEqualTo("Id", id)
                .get()
                 .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()){
                        callback.accept(Boolean.FALSE);
                        return;
                    }
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()){
                        doc.getReference().delete();
                        // technically there should only be one document
                        // but couldn't figure out how to make it work
                    }
                    callback.accept(Boolean.TRUE);
                })
                 .addOnFailureListener(e ->{
                     callback.accept(null);
                 });
    }

    @Override
    public void fetchALLRegistrations(String eventId, Consumer<List<Registration>> callback){
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

    @Override
    public void registrationExists(String id, Consumer<Boolean> callback){
        db.collection("registrations")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        callback.accept(Boolean.TRUE);
                    } else {
                        callback.accept(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(e ->{
                    callback.accept(null);
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

    public void eventExists(Event event, Consumer<Boolean> callback){
        db.collection("actors")
                .document(event.getId())
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

    public void actorExistsByEmail(String email, Consumer<Boolean> callback) {
        db.collection("actors")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(q -> callback.accept(!q.isEmpty()))
                .addOnFailureListener(e -> callback.accept(null)); // null = error path
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
    public void fetchEventById(String eventId, Consumer<Event> callback){
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
    public void getPendingRegistrationsForEvent(String eventId, Consumer<Integer> callback) {
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(waitinglistSnapshot -> {
                    int count = waitinglistSnapshot.size();
                    callback.accept(count);
                })
                .addOnFailureListener(e -> {
                    callback.accept(0); // return 0 on error
                });
    }
    
    /**
     * finds the events which an actor have joined
     * @param eventId
     * @param actor
     */
    public void joinEvent(String eventId, Actor actor) {
        db.collection("registrations")
                .add(new Registration(null, actor.getDeviceIdentifier(), eventId, "Pending"))
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    // id is the its documentId
                    documentReference.update("id", documentId);
                });
    }
    
    /**
     * user's data from the registered collection is deleted if they leave the event
     * @param actor
     * @param eventId
     */
    public void leaveEvent(String eventId, Actor actor) {
        db.collection("registrations")
                .whereEqualTo("entrantId", actor.getDeviceIdentifier())
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
    
    /**
     * gets all events info
     * @param callback
     */
    public void getEvents(Consumer<List<Event>> callback) {
        db.collection("events")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Event> eventList = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setId(doc.getId());
                            eventList.add(event);
                        }
                    }
                    callback.accept(eventList);
                })
                .addOnFailureListener(e -> callback.accept(Collections.emptyList()));
    }
    
    /**
     * finds the events which an actor is registered in
     * (for the buttons in the edit event screen)
     * @param actor: person currently logged in
     * @param callback
     */
    public void getEntrantRegisteredEvents(Actor actor, Consumer<Set<String>> callback) {
        db.collection("registrations")
                .whereEqualTo("entrantId", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(registrationSnapshot -> {
                    Set<String> registeredEventIds = new HashSet<>();
                    for (DocumentSnapshot doc : registrationSnapshot.getDocuments()) {
                        registeredEventIds.add(doc.getString("eventId"));
                    }
                    callback.accept(registeredEventIds);
                });
    }
    
    /**
     * listener so that screen updates immediately after the database does
     * @param callback
     * @return listener
     */
    public ListenerRegistration listenToRegisteredEvents(Actor actor, Consumer<Set<String>> callback) {
        return db.collection("registrations")
                .whereEqualTo("entrantId", actor.getDeviceIdentifier())
                .addSnapshotListener((snapshot, e) -> {
                    Set<String> registeredEventIds = new HashSet<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            registeredEventIds.add(doc.getString("eventId"));
                        }
                    }
                    callback.accept(registeredEventIds);
                });
    }
    
    /**
     * used so that event image and description can be taken from the events collection and displayed
     * @param actor
     * @param callback
     */
    public void getNotificationEventInfo(Actor actor, Consumer<List<Registration>> callback) {
        db.collection("registrations")
                .whereEqualTo("entrantId", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    List<Task<DocumentSnapshot>> eventTasks = new ArrayList<>();
                    for (DocumentSnapshot registerDoc : snapshot.getDocuments()) {
                        Registration register = registerDoc.toObject(Registration.class);
                        if (register != null) {
                            register.setStatus(registerDoc.getString("status"));
                            registrations.add(register);
                            
                            // to be able to display image + description in notification
                            String eventId = register.getEventId();
                            eventTasks.add(db.collection("events").document(eventId).get()
                                    .addOnSuccessListener(eventDoc -> {
                                        if (eventDoc.exists()) {
                                            Event event = eventDoc.toObject(Event.class);
                                            if (event != null) {
                                                register.setDescription(event.getTitle());
                                                register.setImage(event.getPosterId());
                                            }
                                        }
                                    }));
                        }
                    }
                    Tasks.whenAllComplete(eventTasks)
                            .addOnSuccessListener(done -> callback.accept(registrations));
                });
    }
    
    /**
     * gives answer to notifications
     * @param documentId: the event's documentId
     * @param answer: "Accepted" or "Declined"
     */
    public void answerEvent(String documentId, String answer) {
        db.collection("registrations")
                .document(documentId)
                .update("status", answer);
    }
    
    /**
     * gets the role of the actor (ie. organizer, entrant, admin)
     * @param actor
     * @param callback
     */
    public void getActorRole(Actor actor, Consumer<String> callback) {
        db.collection("actors")
                .whereEqualTo("deviceIdentifier", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(roleSnapshot -> {
                    String role = "";
                    for (DocumentSnapshot doc : roleSnapshot.getDocuments()) {
                        role = doc.getString("role");
                        break; // already found it so no need to continue
                    }
                    callback.accept(role);
                });
    }
}

