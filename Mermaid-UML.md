classDiagram
class Actor {
    <<entity>>
    - String deviceIdentifier
    - String name
    - String email
    - String phoneNumber
    + getDeviceIdentifier() String
    + getPhoneNumber() String
    + getEmail() String
    + getRole() String
}
class Event {
    <<entity>>
    - String id
    - String title
    - String posterId
    - String description
    - String registrationDeadline
    - Integer participantCap
    - Boolean recordLocation
    - String qrCodeId
    - String guidelines
    - String criteria
    - String time
    - String location
    - String date
    - String ownerId
    + getPosterId() String
    + getId() String
    + getDate() String
    + getLocation() String
    + getTime() String
    + getOwnerId() String
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
    - Boolean cond
    - String type
    - String message
}
class Session {
    <<entity>>
    - IDatabase database
    - Actor currentActor
    - Event currentEvent
}
class Database {
    <<class>>
    - FirebaseFirestore db
}
Database ..|> IDatabase
class SessionManager {
    <<manager>>
    - ActorManager actorManager
    - EventManager eventManager
    - UserInterfaceManager userInterfaceManager
    - NotificationManager notificationManager
    - NavigationManager navigationManager
    - InvitationManager invitationManager
    - Session session
    - boolean fullInitialization
    + SessionManager()
    + SessionManager(fullInitialization: boolean)
    + getActorManager() ActorManager
    + getEventManager() EventManager
    + getInvitationManager() InvitationManager
    + getNotificationManager() NotificationManager
    + getUserInterfaceManager() UserInterfaceManager
    + getNavigationManager() NavigationManager
    + getSession() Session
    + setSession(session Session)
    + setCurrentActor(actor Actor)
    + isFullInitialization() boolean
}
class ActorManager {
    <<manager>>
    - SessionManager sessionManager
    + ActorManager(sessionManager: SessionManager)
    + ActorManager()
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
    + insertEvent(event: Event, cb: Consumer~Result~)
    + deleteRegistration(reg: Registration, cb: Consumer~Result~)
    + fetchEventById(id: String, cb: Consumer~Result~)
    + fetchAllRegistrations(id: String, cb: Consumer~List~Registration~~)
    + getWaitingListCount(id: String, cb: Consumer~Integer~)
    + addUserToWaitList(id: String, cb: Consumer~Boolean~)
}
class NavigationManager {
    <<manager>>
    - SessionManager sessionManager
    + NavigationManager(sessionManager: SessionManager)
    + setActivity(a: Activity)
    + getActivity(): Activity
    + goTo(targetDestination: Class<*>, flag: navFlags)
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
class UserInterfaceManager {
    <<manager>>
    - SessionManager sessionManager
    + UserInterfaceManager(sessionManager: SessionManager)
    + getSessionManager() SessionManager
    + getSession() Session
    + getCurrentActor() Actor
    + getCurrentEvent() Event
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
    + deleteRegistor(id: String, cb: Consumer~Boolean~)
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
UserInterfaceManager --> SessionManager
UserInterfaceManager --> Session
UserInterfaceManager --> Actor
UserInterfaceManager --> Event
NavigationManager --> SessionManager
NavigationManager --> Session
NavigationManager --> Activity
InvitationManager --> Invitation
InvitationManager --> Actor
InvitationManager --> Event
NotificationManager --> Notification
ActorManager --> SessionManager
ActorManager --> IDatabase
ActorManager --> Result
ActorManager --> Actor
NotificationManager --> Actor
SessionManager *-- ActorManager
SessionManager *-- EventManager
SessionManager *-- UserInterfaceManager
SessionManager *-- NotificationManager
SessionManager *-- NavigationManager
SessionManager *-- InvitationManager
SessionManager *-- UserInterfaceManager
SessionManager *-- Session
SessionManager *-- NavigationManager
SessionManager --> IDatabase
EventManager --> SessionManager
EventManager --> IDatabase
EventManager --> Event
EventManager --> Registration
EventManager --> Result
