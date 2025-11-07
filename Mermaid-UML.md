classDiagram

class Actor {
    <<entity>>
    - String deviceIdentifier
    - String name
    - String email
    - String phoneNumber
    + getDeviceIdentifier() String
}

class Event {
    <<entity>>
    - String id
    - String title
    - String posterId
    - String description
    - Date registrationDeadline
    - int participantCap
    - boolean recordLocation
    - String qrCodeId
    + getId() String
}

class Registration {
    <<entity>>
    - String id
    - String entrantId
    - String eventId
    - String status
    + getId() String
}

class Invitation {
    <<entity>>
    - String id
    - String receivingActorId
    - String eventId
    + getId() String
}

class Notification {
    <<entity>>
    - String id
    - String receivingActorId
    - String sendingActorId
    - String message
    + getId() String
}

class Result {
    <<entity>>
    - String condition
    - String operation
    - String message
}

class ActorManager {
    <<manager>>
    - SessionManager sessionManager
    + ActorManager(sessionManager: SessionManager)
    + actorExistsByEmail(email: String, cb: Consumer~Result~)
    + insertActor(actor: Actor, cb: Consumer~Result~)
    + updateActor(oldActor: Actor, updatedActor: Actor, cb: Consumer~Result~)
    + fetchActorById(id: String, cb: Consumer~Result~)
    + deleteActor(actor: Actor, cb: Consumer~Result~)
    + deleteEntrantCascade(actor: Actor, cb: Consumer~Result~)
}

class EventManager {
    <<manager>>
    - SessionManager sessionManager
    + EventManager(sessionManager: SessionManager)
    + createEvent(event: Event) Event
    + insertEvent(event: Event, cb: Consumer~Result~)
    + deleteRegistration(reg: Registration, cb: Consumer~Result~)
    + fetchEventById(id: String, cb: Consumer~Result~)
    + fetchAllRegistrations(id: String, cb: Consumer~List~Registration~~)
    + getWaitingListCount(id: String, cb: Consumer~Integer~)
    + addUserToWaitList(id: String, cb: Consumer~Boolean~)
}

class InvitationManager {
    <<manager>>
    - SessionManager sessionManager
    + InvitationManager(sessionManager: SessionManager)
}

class NotificationManager {
    <<manager>>
    - SessionManager sessionManager
    + NotificationManager(sessionManager: SessionManager)
}

class IDatabase {
    <<interface>>
    + deleteActor(actor: Actor, cb: Consumer~Boolean~)
    + insertActor(actor: Actor, cb: Consumer~Boolean~)
    + updateActor(oldActor: Actor, updatedActor: Actor, cb: Consumer~Boolean~)
    + actorExists(actor: Actor, cb: Consumer~Boolean~)
    + actorExistsByEmail(email: String, cb: Consumer~Boolean~)
    + deleteEntrantCascade(deviceId: String, cb: Consumer~Boolean~)
    + fetchActorById(id: String, cb: Consumer~Actor~)
    + getAvailableEvents(actor: Actor, cb: Consumer~List~Event~~)
    + insertEvent(event: Event, cb: Consumer~Boolean~)
    + updateImage(eventId: String, posterIds: String)
    + deleteRegistration(id: String, cb: Consumer~Boolean~)
    + fetchAllRegistrations(id: String, cb: Consumer~List~Registration~~)
    + getPendingRegistrations(id: String, cb: Consumer~Integer~)
    + registrationExists(id: String, cb: Consumer~Boolean~)
    + addUserToWaitList(id: String, actor: Actor, cb: Consumer~Boolean~)
    + fetchEventById(id: String, cb: Consumer~Event~)
    + joinEvent(id: String, actor: Actor)
    + leaveEvent(id: String, actor: Actor)
    + getEvents(cb: Consumer~List~Event~~)
    + getEntrantRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~)
    + listenToRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~) ListenerRegistration
    + getNotificationEventInfo(actor: Actor, cb: Consumer~List~Registration~~)
    + answerEvent(docId: String, answer: String)
}

Actor "1" --> "many" Registration
Actor "1" --> "many" Invitation
Actor "1" --> "many" Notification

Event "1" --> "many" Registration
Event "1" --> "many" Invitation

ActorManager --> Actor
EventManager --> Event
InvitationManager --> Invitation
NotificationManager --> Notification
RegistrationManager --> Registration

ActorManager --> SessionManager
ActorManager --> IDatabase
ActorManager --> Result
RegistrationManager --> Actor
RegistrationManager --> Event
InvitationManager --> Actor
InvitationManager --> Event
NotificationManager --> Actor
