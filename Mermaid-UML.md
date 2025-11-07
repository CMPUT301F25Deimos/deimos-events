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
    - Number participantCap
    - Boolean recordLocation
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

Actor "1" --> "many" Registration
Actor "1" --> "many" Invitation 
Actor "1" --> "many" Notification

Event "1" --> "many" Registration
Event "1" --> "many" Invitation

