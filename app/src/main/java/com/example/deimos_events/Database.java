package com.example.deimos_events;

import android.util.Log;

import com.example.deimos_events.ui.notifications.NotificationsArrayAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Database implements IDatabase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insertActor(Actor actor, Consumer<Boolean> callback) {
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                //.document(actor.getDeviceIdentifier())
                .set(actor)
                .addOnSuccessListener(tempVoid -> {
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    public void updateActor(Actor oldActor, Actor updatedActor, Consumer<Boolean> callback) {

        db.collection("actors")
                .document(oldActor.getDeviceIdentifier())
                .update(
                        "name", updatedActor.getName(),
                        "email", updatedActor.getEmail(),
                        "phoneNumber", updatedActor.getPhoneNumber(),
                        "role", updatedActor.getRole(),
                        "notificationsPreference", updatedActor.getNotificationsPreference())
                .addOnSuccessListener(tempVoid -> {
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });

    }

    public void deleteActor(Actor actor, Consumer<Boolean> callback) {
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .delete()
                .addOnSuccessListener(tempVoid -> {
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

    //    public void insertEvent(Event event, Consumer<Boolean> callback){
//        db.collection("events")
//                .document()
//                .set(event)
//                .addOnSuccessListener(e ->{
//                    callback.accept(Boolean.TRUE);
//                })
//                .addOnFailureListener(e ->{
//                    callback.accept(Boolean.FALSE);
//                });
//    }
    public void insertEvent(Event event, Consumer<Boolean> callback) {
        // Create a new Firestore document reference first
        DocumentReference ref = db.collection("events").document();

        // Store the Firestore document ID inside the event object
        event.setId(ref.getId());

        ref.set(event)
                .addOnSuccessListener(v -> callback.accept(Boolean.TRUE))
                .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
    }


    @Override
    public void updateImage(String eventId, String posterIdArray, Consumer<Boolean> callback) {
        db.collection("events")
                .whereEqualTo("id", eventId)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);

                        doc.getReference().update("posterId", posterIdArray)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("EventManager", "Image updated successfully");
                                    callback.accept(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EventManager", "Update failed: " + e.getMessage());
                                    callback.accept(false);
                                });

                    } else {
                        Log.e("EventManager", "No document found for eventId: " + eventId);
                        callback.accept(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventManager", "Query failed: " + e.getMessage());
                    callback.accept(false);
                });
    }


    @Override
    public void deleteRegistor(String id, Consumer<Boolean> callback) {
        db.collection("registrations")
                .whereEqualTo("Id", id)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.accept(Boolean.FALSE);
                        return;
                    }
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                        // technically there should only be one document
                        // but couldn't figure out how to make it work
                    }
                    callback.accept(Boolean.TRUE);
                })
                .addOnFailureListener(e -> {
                    callback.accept(null);
                });
    }

    @Override
    public void fetchALLRegistrations(String eventId, Consumer<List<Registration>> callback) {
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
    public void fetchAllEntrantsEnrolled(String eventId, Consumer<List<Entrant>> callback) {
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(registrationSnapshot -> {
                    if (registrationSnapshot.isEmpty()) {
                        callback.accept(Collections.emptyList());
                        return;
                    }
                    List<String> entrantIds = new ArrayList<>();
                    for (DocumentSnapshot doc : registrationSnapshot.getDocuments()) {
                        String entrantId = doc.getString("entrantId");
                        if (entrantId != null) {
                            entrantIds.add(entrantId);
                        }
                    }
                    if (entrantIds.isEmpty()) {
                        callback.accept(Collections.emptyList());
                        return;
                    }
                    db.collection("entrants")
                            .whereIn(FieldPath.documentId(), entrantIds)
                            .get()
                            .addOnSuccessListener(entrantSnapshot -> {
                                List<Entrant> entrants = new ArrayList<>();
                                for (DocumentSnapshot doc : entrantSnapshot.getDocuments()) {
                                    Entrant entrant = doc.toObject(Entrant.class);
                                    entrants.add(entrant);
                                }
                                callback.accept(entrants);
                            })
                            .addOnFailureListener(e -> {
                                System.err.println("Error fetching entrants: " + e.getMessage());
                                callback.accept(Collections.emptyList());
                            });
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error getting registrations: " + e.getMessage());
                    callback.accept(Collections.emptyList());
                });
    }


    @Override
    public void registrationExists(String id, Consumer<Boolean> callback) {
        db.collection("registrations")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.accept(Boolean.TRUE);
                    } else {
                        callback.accept(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(e -> {
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

    public void actorExists(Actor actor, Consumer<Boolean> callback) {
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                //.document(actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.accept(Boolean.TRUE);
                    } else {
                        callback.accept(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.accept(null);
                });
    }

    public void eventExists(Event event, Consumer<Boolean> callback) {
        String id = event.getId();

        // If id is null or empty, clearly the event does not exist yet
        if (id == null || id.isEmpty()) {
            callback.accept(Boolean.FALSE);
            return;
        }

        db.collection("events")
                .document(id)
                .get()
                .addOnSuccessListener(doc -> callback.accept(doc.exists()))
                .addOnFailureListener(e -> callback.accept(null));
    }


//    public void eventExists(Event event, Consumer<Boolean> callback){
//        db.collection("actors")
//                .document(event.getId())
//                .get()
//                .addOnSuccessListener(doc ->{
//                    if (doc.exists()){
//                        callback.accept(Boolean.TRUE);
//                    }else{
//                        callback.accept(Boolean.FALSE);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    callback.accept(null);
//                });
//    }

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

    public void addUserToWaitList(String eventId, Actor actor, Consumer<Boolean> callback) {
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Waiting")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean alreadyExists = false;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String entrantId = doc.getString("entrantId");
                        if (entrantId != null && entrantId.equals(actor.getDeviceIdentifier())) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (alreadyExists) {
                        callback.accept(false);
                    } else {
                        Map<String, Object> registrationData = new HashMap<>();
                        registrationData.put("entrantId", actor.getDeviceIdentifier());
                        registrationData.put("eventId", eventId);
                        registrationData.put("status", "Pending");

                        db.collection("registrations")
                                .add(registrationData)
                                .addOnSuccessListener(ref -> callback.accept(true))
                                .addOnFailureListener(e -> callback.accept(false));


                    }
                })
                .addOnFailureListener(e -> callback.accept(false));
    }

    public void fetchEventById(String eventId, Consumer<Event> callback) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.accept(event);
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
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
     *
     * @param eventId the id of the event
     * @param actor the actor who is joining the event
     */
    public void joinEvent(String eventId, Actor actor) {
        db.collection("registrations")
                .add(new Registration(null, actor.getDeviceIdentifier(), eventId, "Waiting"))
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    // id is the its documentId
                    documentReference.update("id", documentId);
                });
    }

    /**
     * user's data from the registered collection is deleted if they leave the event
     *
     * @param actor: The actor who is leaving the event
     * @param eventId: the id of the event the actor is leaving
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
     *
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
     *
     * @param actor:   person currently logged in
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
     * listener for events so that screen updates immediately after the database does
     *
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
     * gives answer to notifications
     *
     * @param documentId: the event's documentId
     * @param answer:     "Accepted" or "Declined"
     */
    public void answerEvent(String documentId, String answer) {
        db.collection("registrations")
                .document(documentId)
                .update("status", answer);
    }

    /**
     * gets the role of the actor (ie. organizer, entrant, admin)
     *
     * @param actor the person whose role is being found
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
    
    /**
     * Gets the notification preferences of the actor (ie. whether they want to be notified or not)
     *
     * @param actor: actor whose preference for notifications is being found
     * @param callback
     */
    public void getNotificationsPreference(Actor actor, Consumer<Boolean> callback) {
        db.collection("actors")
                .whereEqualTo("deviceIdentifier", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(roleSnapshot -> {
                    Boolean notification = null;
                    for (DocumentSnapshot doc : roleSnapshot.getDocuments()) {
                        notification = doc.getBoolean("notificationsPreference");
                        break; // already found it so no need to continue
                    }
                    callback.accept(notification);
                });
    }
    
    /**
     * Changes their notification preference
     * @param actor: the user whose notification preferences will be changed
     * @param notificationsPreference: whether the user wants to be able to receive notifications or not
     */
    public void setNotificationsPreference(Actor actor, Boolean notificationsPreference) {
        db.collection("actors")
                .document(actor.getDeviceIdentifier())
                .update("notificationsPreference", notificationsPreference);
    }
    
    /**
     * Gets the names of the people that will be messaged by the organizer
     * (eg. rejected, waitinglist, declined, "Jane Doe", etc.) into all names instead of including "Waitinglist", etc.
     * (ie. combines list of names with people who were rejected, in waitinglist, declined, and "Jane Doe")
     * @param eventId id of the event that user is finding the available notifications receivers of
     * @param recipients: people who have joined the event and have notifs on
     * @param callback
     */
    public void getNotificationReceivers(String eventId, List<String> recipients, Consumer<List<Map<String, String>>> callback) {
        // FInal people list (b/c given list could have "Accepted", "Waitlisted", etc, and this expands on that
        // and also looks for people part of those groups)
        List<Map<String, String>> fullRecipients = new ArrayList<>();
        Set<String> found = new HashSet<>(); // to avoid duplicates
        List<String> statuses = Arrays.asList("Everyone", "Waitlisted", "Pending", "Accepted", "Cancelled", "Rejected Waitlist");
        
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(eventDocs -> {
                    
                    List<String> groupUserIds = new ArrayList<>(); // everyone
                    Map<String, String> registrationMap = new HashMap<>(); // finds entrantId and registrationId of people
                    
                    for (DocumentSnapshot doc : eventDocs) {
                        String entrantId = doc.getString("entrantId");
                        String regId = doc.getId();
                        
                        if (entrantId != null) {
                            groupUserIds.add(entrantId);
                            registrationMap.put(entrantId, regId);
                        }
                    }
                    
                    // keeps if status is part of the list above
                    List<String> filteredRecipients = new ArrayList<>();
                    for (String recipient : recipients) {
                        if (statuses.contains(recipient) || recipient.trim().length() > 0) {
                            filteredRecipients.add(recipient);
                        }
                    }
                    
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    if (filteredRecipients.contains("Everyone") && !groupUserIds.isEmpty()) {
                        tasks.add(
                                db.collection("actors")
                                        .whereEqualTo("notificationsPreference", true)
                                        .whereIn(FieldPath.documentId(), groupUserIds)
                                        .get()
                        );
                    }
                    
                    // finds names of people from list
                    List<String> nameFilters = new ArrayList<>();
                    for (String filter : filteredRecipients) {
                        if (!statuses.contains(filter)) {
                            nameFilters.add(filter);
                        }
                    }
                    
                    if (!nameFilters.isEmpty() && !groupUserIds.isEmpty()) {
                        tasks.add(
                                db.collection("actors")
                                        .whereEqualTo("notificationsPreference", true)
                                        .whereIn("name", nameFilters)
                                        .whereIn(FieldPath.documentId(), groupUserIds)
                                        .get()
                        );
                    }
                    
                    // finds people whose registration status are part of the above list
                    if (!filteredRecipients.isEmpty()) {
                        tasks.add(
                                db.collection("registrations")
                                        .whereIn("status", filteredRecipients)
                                        .get()
                        );
                    }
                    
                    if (tasks.isEmpty()) {
                        callback.accept(fullRecipients);
                        return;
                    }
                    
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                        
                        Map<String, String> actorMap = new HashMap<>();
                        Set<String> idsFromGroups = new HashSet<>();
                        
                        for (Object result : results) {
                            QuerySnapshot snapshots = (QuerySnapshot) result;
                            
                            // finds people from registrations with above status
                            if (!snapshots.isEmpty() && snapshots.getDocuments().get(0).contains("status")) {
                                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                    String entrantId = doc.getString("entrantId");
                                    idsFromGroups.add(entrantId);
                                }
                            } else { // actors who were chosen
                                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                    actorMap.put(doc.getId(), doc.getString("name"));
                                }
                            }
                        }
                        
                        // combine both result
                        // add people with given status(es)
                        for (String actorId : idsFromGroups) {
                            if (registrationMap.containsKey(actorId) && found.add(actorId)) {
                                Map<String, String> entry = new HashMap<>();
                                entry.put("deviceIdentifier", actorId);
                                entry.put("name", actorMap.get(actorId));
                                entry.put("registrationId", registrationMap.get(actorId));
                                fullRecipients.add(entry);
                            }
                        }
                        
                        for (String actorId : actorMap.keySet()) {
                            if (registrationMap.containsKey(actorId) && found.add(actorId)) {
                                Map<String, String> entry = new HashMap<>();
                                entry.put("deviceIdentifier", actorId);
                                entry.put("name", actorMap.get(actorId));
                                entry.put("registrationId", registrationMap.get(actorId));
                                fullRecipients.add(entry);
                            }
                        }
                        callback.accept(fullRecipients);
                    });
                });
    }
    
    /**
     * Saves notifications in database
     * @param sender: id of the person sending a message
     * @param recipientId: if of the person receiving a message
     * @param message: the message which the sender is sending
     * @param eventId: the id of the event which the sender is notifying a recipient about
     * @param registrationId: the id of the recipient's registration for the aforementioned event
     */
    public void setNotifications(String sender, String recipientId, String message, String eventId, String registrationId) {
        // TODO: get the notification from the sender
        db.collection("notifications")
                .add(new Notifications(null, eventId, message, sender, recipientId, registrationId))
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    // id is the its documentId
                    documentReference.update("id", documentId);
                });
    }
    
    /**
     * Gets notifications from database
     * @param actor
     * @param notificationsList
     * @param adapter
     */
    public void getNotifications(Actor actor, ArrayList<Notifications> notificationsList, NotificationsArrayAdapter adapter) {
        db.collection("notifications")
                .whereEqualTo("recipientId", actor.getDeviceIdentifier())
                .get()
                .addOnSuccessListener(snapshot -> {
                    notificationsList.clear();
                    List<Task<?>> allTasks = new ArrayList<>();
                    
                    for (DocumentSnapshot notifyDoc : snapshot.getDocuments()) {
                        Notifications notify = notifyDoc.toObject(Notifications.class);
                        if (notify == null) {
                            continue;
                        }
                        
                        // set registrationId in notifications
                        notify.setRegistrationId(notifyDoc.getString("registrationId"));
                        
                        // set time of notification
                        Timestamp time = notifyDoc.getTimestamp("time");
                        notify.setTime(time != null ? time.toDate() : new Date());
                        
                        notificationsList.add(notify);
                        
                        // gets event image
                        Task<DocumentSnapshot> eventTask = db.collection("events")
                                .document(notify.getEventId())
                                .get()
                                .addOnSuccessListener(eventDoc -> {
                                    if (eventDoc.exists()) {
                                        Event event = eventDoc.toObject(Event.class);
                                        if (event != null) {
                                            notify.setTitle(event.getTitle());
                                            notify.setImage(event.getPosterId());
                                        }
                                    }
                                });
                        allTasks.add(eventTask);
                        
                        // gets status
                        Task<Void> statusTask = db.collection("notifications")
                                .document(notify.getId())
                                .get()
                                .continueWithTask(notificationSnapTask -> {
                                    DocumentSnapshot notificationDoc = notificationSnapTask.getResult();
                                    if (notificationDoc != null && notificationDoc.exists()) {
                                        String currentStatus = notificationDoc.getString("status");
                                        
                                        // if "Waiting" is not the current status, be able to change status between notif and register collection parallelly (to answer)
                                        if (!"Waiting".equals(currentStatus)) {
                                            String registrationId = notify.getRegistrationId();
                                            return db.collection("registrations")
                                                    .document(registrationId)
                                                    .get()
                                                    .continueWithTask(regSnapTask -> {
                                                        DocumentSnapshot regDoc = regSnapTask.getResult();
                                                        if (regDoc != null && regDoc.exists()) {
                                                            String newStatus = regDoc.getString("status");
                                                            notify.setStatus(newStatus);
                                                            return db.collection("notifications")
                                                                    .document(notify.getId())
                                                                    .update("status", newStatus)
                                                                    .continueWithTask(t -> Tasks.forResult(null));
                                                        }
                                                        return Tasks.forResult(null);
                                                    });
                                        }
                                    }
                                    return Tasks.forResult(null);
                                });
                        allTasks.add(statusTask);
                    }
                    
                    Tasks.whenAll(allTasks).addOnSuccessListener(v -> {
                        // sort to most recent notif
                        notificationsList.sort((n1, n2) -> {
                            Date t1 = n1.getTime();
                            Date t2 = n2.getTime();
                            return t2.compareTo(t1);
                        });
                        adapter.notifyDataSetChanged();
                    });
                });
    }
    
    /**
     * Sets status of registration
     * @param registrationId: registration id of an entrant's registration for an event
     * @param registrationStatus: the status of an entrant to an event (eg. Waitlisted, Pending, etc.)
     */
    public void setRegistrationStatus(String registrationId, String registrationStatus) {
        db.collection("registrations")
                .document(registrationId)
                .update("status", registrationStatus);
    }
    
        @Override
    public void deleteEventCascade(String eventId, java.util.function.Consumer<Boolean> callback) {
        // 1) Find all registrations for this event
        db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(regSnap -> {
                    // 2) Use a batch to delete registrations + the event itself
                    WriteBatch batch = db.batch();

                    // Delete all registrations tied to this event
                    for (DocumentSnapshot doc : regSnap.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    // Delete the event document itself.
                    // NOTE: eventId is the Firestore document ID (you set it in getEvents()).
                    batch.delete(db.collection("events").document(eventId));

                    // 3) Commit batch
                    batch.commit()
                            .addOnSuccessListener(v -> callback.accept(Boolean.TRUE))
                            .addOnFailureListener(e -> callback.accept(Boolean.FALSE));
                })
                .addOnFailureListener(e -> {
                    callback.accept(Boolean.FALSE);
                });
    }

}

