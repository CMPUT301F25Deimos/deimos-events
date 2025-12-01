---
config:
  layout: elk
---
classDiagram

class EventsApp {
    <<application>>
    - SessionManager sessionManager

    + onCreate(): void
    + getSessionManager(): SessionManager
}

class Actor {
    <<entity>>
    - String deviceIdentifier
    - String name
    - String email
    - String phoneNumber
    - Boolean notificationsPreference
    - String role
    + Actor(deviceIdentifier: String, name: String, email: String, phoneNumber: String, role: String?, notificationsPreference: Boolean)
    + Actor()
    + Actor(deviceIdentifier: String, name: String, email: String, phoneNumber: String, notificationsPreference: Boolean)
    + getDeviceIdentifier() String
    + getPhoneNumber() String
    + getEmail() String
    + getName() String
    + getRole() String
    + getNotificationsPreference() Boolean
}


class Administrator {
    <<entity>>
    + Administrator(deviceIdentifier: String, name: String, email: String, phoneNumber: String, notificationsPreference: Boolean)
}

Administrator --|> Actor

class Entrant {
    <<entity>>
    + Entrant(deviceIdentifier: String, name: String, email: String, phoneNumber: String, notificationsPreference: Boolean)
}

Entrant --|> Actor

class Organizer {
    <<entity>>
    + Organizer(deviceIdentifier: String, name: String, email: String, phoneNumber: String, notificationsPreference: Boolean)
}

Organizer --|> Actor



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

    + Event(id: String, 
            title: String, 
            posterId: String, 
            description: String,
            registrationDeadline: String, 
            participantCap: Integer,
            recordLocation: Boolean, 
            qrCodeId: String, 
            ownerId: String,
            guidelines: String, 
            criteria: String
            )

    + Event()

    + getTitle() String
    + setTitle(title: String)
    + getPosterId() String
    + setPosterId(posterId: String)
    + getDescription() String
    + setDescription(description: String)
    + getRegistrationDeadline() String
    + setRegistrationDeadline(deadline: String)
    + getParticipantCap() Number
    + setParticipantCap(participantCap: Integer)
    + getRecordLocation() Boolean
    + setRecordLocation(recordLocation: Boolean)
    + getQrCodeId() String
    + setQrCodeId(qrCodeId: String)
    + getId() String
    + setId(id: String)
    + setCriteria(criteria: String)
    + setGuidelines(guidelines: String)
    + getGuidelines() String
    + getCriteria() String
    + setDate(date: String)
    + setLocation(location: String)
    + setTime(time: String)
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
    + String latitude
    + String longitude
    + String title
    + String image

    + Registration()
    + Registration(
                id: String, 
                entrantId: String, 
                eventId: String,
                status: String, 
                latitude: String, 
                longitude: String
                )

    + getId() String
    + setId(id: String)

    + getEntrantId() String
    + setEntrantId(entrantId: String)

    + getEventId() String
    + setEventId(eventId: String)

    + getStatus() String
    + setStatus(status: String)

    + setTitle(title: String)
    + setImage(image: String)

    + getLongitude() String
    + getLatitude() String
}

class Invitation {
    <<entity>>
    - String id
    - String receivingActorId
    - String eventId

    + Invitation(id: String, receivingActorId: String, eventId: String)

    + getId() String
    + setId(id: String)
    + getReceivingActorId() String
    + setReceivingActorId(receivingActorId: String)
    + getEventId() String
    + setEventId(eventId: String)
}

class Notifications {
    <<entity>>
    - String id
    - String eventId
    - String message
    - String senderId
    - String recipientId
    - String registrationId
    - String title
    - String image
    - String status
    - Date time

    + Notifications()
    + Notifications(id: String, 
                    eventId: String, 
                    message: String,
                    senderId: String, 
                    recipientId: String,
                    registrationId: String
                    )

    + getId() String
    + setId(id: String)

    + getEventId() String
    + setEventId(eventId: String)

    + getMessage() String
    + setMessage(message: String)

    + getSenderId() String
    + setSenderId(senderId: String)

    + getRecipientId() String
    + setRecipientId(recipientId: String)

    + getRegistrationId() String
    + setRegistrationId(registrationId: String)

    + getTitle() String
    + setTitle(title: String)

    + getImage() String
    + setImage(image: String)

    + getStatus() String
    + setStatus(status: String)

    + getTime() Date
    + setTime(time: Date)
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

class IDatabase {
    <<interface>>
    + deleteActor(actor: Actor, cb: Consumer~Boolean~)
    + insertActor(actor: Actor, cb: Consumer~Boolean~)
    + updateActor(oldActor: Actor, updatedActor: Actor, cb: Consumer~Boolean~)
    + actorExists(actor: Actor, cb: Consumer~Boolean~)
    + getAvailableEvents(actor: Actor, cb: Consumer~List~Event~~)
    + fetchActorByID(id: String, cb: Consumer~Actor~)
    + actorExistsByEmail(email: String, cb: Consumer~Boolean~)
    + deleteEntrantCascade(deviceIdentifier: String, cb: Consumer~Boolean~)
    + eventExists(event: Event, cb: Consumer~Boolean~)
    + insertEvent(event: Event, cb: Consumer~Boolean~)
    + updateImage(eventId: String, posterIdArray: String, cb: Consumer~Boolean~)
    + fetchALLRegistrations(eventId: String, cb: Consumer~List~Registration~~)
    + fetchAllEntrantsEnrolled(eventId: String, cb: Consumer~List~Entrant~~)
    + getRegistrationsByStatus(eventId: String, status: String, cb: Consumer~List~Registration~~)
    + getWaitingRegistrationsForEvent(eventId: String, cb: Consumer~Integer~)
    + deleteRegistration(registrationId: String, cb: Consumer~Boolean~)
    + addUserToWaitList(eventId: String, actor: Actor, cb: Consumer~Boolean~)
    + fetchEventById(eventId: String, cb: Consumer~Event~)
    + joinEvent(context: Context, eventId: String, actor: Actor, cb: Consumer~Boolean~)
    + leaveEvent(eventId: String, actor: Actor, cb: Consumer~Boolean~)
    + getEvents(cb: Consumer~List~Event~~)
    + getEntrantRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~)
    + listenToRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~) ListenerRegistration
    + answerEvent(documentId: String, answer: String, cb: Consumer~Boolean~)
    + getActorRole(actor: Actor, cb: Consumer~String~)
    + getNotificationsPreference(actor: Actor, cb: Consumer~Boolean~)
    + setNotificationsPreference(actor: Actor, notificationsPreference: Boolean, cb: Consumer~Boolean~)
    + getNotificationReceivers(eventId: String, recipients: List~String~, cb: Consumer~List~Map~String,String~~~)
    + setNotifications(sender: String, recipient: String, message: String, eventId: String, registrationId: String, cb: Consumer~Boolean~)
    + getNotifications(actor: Actor, notificationsList: List~Notifications~, adapter: NotificationsArrayAdapter)
    + setRegistrationStatus(registrationId: String, registrationStatus: String, cb: Consumer~Boolean~)
    + deleteEventCascade(eventId: String, cb: Consumer~Boolean~)
    + getAllActors(cb: Consumer~List~Actor~~)
    + deleteEventImage(eventId: String, cb: Consumer~Boolean~)
    + getNotificationAdmin(adapter: NotificationsAdminArrayAdapter, notificationsList: List~Notifications~)
    + getNotificationEventInfo(actor: Actor, cb: Consumer~List~Registration~~)
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
    + SessionManager(fullInitialization: boolean
    + getInvitationManager() InvitationManager
    + getNotificationManager() NotificationManager
    + getUserInterfaceManager() UserInterfaceManager
    + getNavigationManager() NavigationManager
    + getSession() Session
    + getEventManager() EventManager
    + getActorManager() ActorManager
    + setSession(session: Session)
    + isFullInitialization() boolean
    - checkFullInitialization(obj: Object) void
    - checkObjectInitialized(name: String, value: T) T
}

class ActorManager {
    <<manager>>
    - SessionManager sessionManager

    + ActorManager(sessionManager: SessionManager)
    + ActorManager()

    + actorExistsByid(id: String, cb: Consumer~Actor~)
    + actorExistsByEmail(email: String, cb: Consumer~Result~)
    + insertActor(actor: Actor, cb: Consumer~Result~)
    + updateActor(oldActor: Actor, updatedActor: Actor, cb: Consumer~Result~)
    + fetchActorByID(id: String, cb: Consumer~Result~)
    + deleteActor(actor: Actor, cb: Consumer~Result~)
    + deleteEntrantCascade(actor: Actor, cb: Consumer~Result~)
    + getAllActors(cb: Consumer~List~Actor~~)
    + fetchActorRole(actor: Actor, cb: Consumer~Result~)
}

class EventManager {
    <<manager>>
    - SessionManager sessionManager

    + EventManager(sessionManager: SessionManager)

    + createEvent(
                id: String, 
                title: String, 
                posterId: Bitmap,
                description: String, 
                registrationDeadline: Date,
                participantCap: Number, 
                recordLocation: Boolean,
                qrCodeId: BitMatrix, 
                critria: String, guidlines: String
                ) Event

    + insertEvent(event: Event, cb: Consumer~Result~)
    + deleteRegistration(registrationId: String, cb: Consumer~Result~)
    + fetchEventById(eventId: String, cb: Consumer~Result~)
    + fetchAllRegistrations(eventId: String, cb: Consumer~List~Registration~~)
    + getWaitingListCount(eventId: String, cb: Consumer~Integer~)
    + getActorById(actorId: String, cb: Consumer~Actor~)
    + addUserToWaitList(eventId: String, cb: Consumer~Boolean~)
    + updateImage(eventId: String, imageBit: Bitmap, cb: Consumer~Boolean~)

    + getAllEvents(cb: Consumer~List~Event~~)
    + adminDeleteEvent(event: Event, cb: Consumer~Result~)
    + exportEntrantsCsv(eventId: String, cb: Consumer~String~)
    + getRegistrationsByStatus(eventId: String, status: String, cb: Consumer~List~Registration~~)
    + deleteEventImage(eventId: String, cb: Consumer~Boolean~)

    + setRegistrationStatus(registrationId: String, registrationStatus: String, cb: Consumer~Result~)
    + answerEvent(registrationId: String, answer: String, cb: Consumer~Result~)

    + fetchNotificationEventInfo(actor: Actor, cb: Consumer~List~Registration~~)
    + listenToRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~) ListenerRegistration
    + fetchEntrantRegisteredEvents(actor: Actor, cb: Consumer~Set~String~~)
    + fetchEvents(cb: Consumer~List~Event~~)

    + leaveEvent(eventId: String, actor: Actor, cb: Consumer~Result~)
    + joinEvent(context: Context, eventId: String, actor: Actor, cb: Consumer~Result~)
}
class NavigationManager {
    <<manager>>
    - SessionManager sessionManager

    + NavigationManager(sessionManager: SessionManager)
    + setActivity(a: Activity)
    + getActivity() Activity
    + goTo(targetDestination: Class<*>, flag: navFlags)
}

class navFlags {
    <<enum>>
    RETURN_TO_TASK
    RESET_TO_NEW_ROOT
    NO_FLAGS
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
    + insertNotifications(
                        sender: String,
                         recipientId: String,
                         message: String,
                         eventId: String,
                         registrationId: String,
                         cb: Consumer~Result~
                         )
    + fetchNotifications(
                         actor: Actor,
                         notificationsList: ArrayList~Notifications~,
                         adapter: NotificationsArrayAdapter
                         )
    + fetchNotificationReceivers(
                                eventId: String,
                                recipients: List~String~,
                                cb: Consumer~List~Map~String,String~~
                                )
    + insertNotificationsPreference(
                                actor: Actor,
                                preference: boolean,
                                cb: Consumer~Result~
                                )
    + fetchNotificationsPreference(
                                actor: Actor,
                                cb: Consumer~Boolean~
                                )
}

class UserInterfaceManager {
    <<manager>>
    - SessionManager sessionManager
    + UserInterfaceManager(sessionManager: SessionManager)
    + getCurrentActor() Actor
    + setCurrentActor(actor: Actor)
    + clearCurrentActor()
    + getCurrentEvent() Event
}

Actor "1" --> "many" Registration
Actor "1" --> "many" Invitation
Actor "1" --> "many" Notification
Event "1" --> "many" Registration
Event "1" --> "many" Invitation

EventsApp --|> Application
EventsApp --> SessionManager



Session --> IDatabase
Session --> Actor
Session --> Event

SessionManager *-- Session
SessionManager *-- ActorManager
SessionManager *-- EventManager
SessionManager *-- UserInterfaceManager
SessionManager *-- NotificationManager
SessionManager *-- NavigationManager
SessionManager *-- InvitationManager
SessionManager --> IDatabase

ActorManager --> SessionManager
ActorManager --> IDatabase
ActorManager --> Result
ActorManager --> Actor

EventManager --> SessionManager
EventManager --> IDatabase
EventManager --> Event
EventManager --> Registration
EventManager --> Result
EventManager --> Actor
EventManager --> Notifications

InvitationManager --> SessionManager

NotificationManager --> SessionManager
NotificationManager --> IDatabase
NotificationManager --> Actor
NotificationManager --> Notifications
NotificationManager --> NotificationsArrayAdapter
NotificationManager --> NotificationsAdminArrayAdapter
NotificationManager --> Result

UserInterfaceManager --> SessionManager
UserInterfaceManager --> Actor
UserInterfaceManager --> Event

NavigationManager --> SessionManager
NavigationManager --> Activity
NavigationManager --> navFlags

class SignupActivity {
    <<activity>>
    - SessionManager SM
    - UserInterfaceManager UIM
    - NavigationManager NM
    - ActorManager AM

    + onCreate(savedInstanceState: Bundle)
    - submit() void
    - createActorByRole(
                        id: String, name: 
                        String, email: 
                        String,
                        phone: String, 
                        role: String,
                        notificationsPreference: Boolean
                        ) Actor
    - clearErrors() void
    - txt(et: TextInputEditText) String
    - hasAllowedDomain(email: String) boolean
}

class CreateFragment {
    <<fragment>>
    - FragmentCreateEventBinding binding
    - CreateViewModel viewModel
    - Button upload
    - ImageView image
    - EditText title
    - EditText Description
    - EditText month
    - EditText day
    - EditText cap
    - EditText year
    - EditText guide
    - EditText criteria
    - TextView desc
    - TextView registration
    - TextView capped
    - TextView open
    - TextView geo
    - Switch location
    - Button create

    + onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
                ) View
    + onDestroyView() void
}



class CreateViewModel {
    <<ViewModel>>
    - MutableLiveData<String> message

    + getMessage() LiveData<String>
    + updateMessage(newMessage: String)
}

class EditFragment {
    <<fragment>>
    - Button update
    - ImageView image
    - String eventId
    - EventManager EM
    - EditViewModel viewModel
    - Button notify
    - Button map

    + onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
                ) View
}

class EditViewModel {
    <<ViewModel>>
    - MutableLiveData<String> message

    + getMessage() LiveData<String>
    + updateMessage(newMessage: String) void
}



class AdministratorsEventsAdapter {
    <<Adapter>>
    - List<Event> events

    + submitList(list: List<Event>) void
    + removeEvent(position: int) void
    + getEventAt(position: int) Event
    + getItemCount() int
}

class AdminEventViewHolder {
    <<ViewHolder>>
    - ImageView image
    - TextView title
    - TextView desc
    - TextView meta
}

class EventArrayAdapter {
    <<Adapter>>
    - Set<String> registeredEventIds
    - Actor actor
    - SessionManager sm
    - EventManager EM
    - ActorManager AM
    - NavController navControl
    - Map<String, String> registrationStatuses
    - boolean historyMode

    + EventArrayAdapter(
                        context: Context,
                        events: List<Event>,
                        registeredEventIds: Set<String>,
                        sm: SessionManager,
                        actor: Actor,
                        navController: NavController
                        )

    + EventArrayAdapter(
                        context: Context,
                        events: List<Event>,
                        registeredEventIds: Set<String>,
                        registrationStatuses: Map<String, String>,
                        historyMode: boolean,
                        sm: SessionManager,
                        actor: Actor,
                        navController: NavController
                        )

    + getView(
                position: int,
                convertView: View?,
                parent: ViewGroup
                ) View

    + updateJoinedEvents(newJoinedEventIds: Set<String>) void

    - changeButtonLook(button: MaterialButton, event: Event) void
}

class EventInformationActivity {
    <<activity>>
    - TextView EventTitle
    - TextView description
    - TextView Guidelines
    - TextView Criteria
    - TextView Location
    - TextView eventDate
    - TextView eventTime
    - TextView waitlisted
    - Button returnButton
    - Button signUpButton
    - ImageView eventPoster
    - SessionManager SM
    - UserInterfaceManager UIM
    - EventManager EM

    + onCreate(savedInstanceState: Bundle) void
}

class EventsAdministratorsFragment {
    <<fragment>>
    - FragmentAdministratorsEventsBinding binding
    - SessionManager sessionManager
    - EventManager eventManager
    - AdministratorsEventsAdapter adapter
    - Paint swipePaint

    + onCreate(savedInstanceState: Bundle) void
    + onCreateView(
                    inflater: LayoutInflater,
                   container: ViewGroup,
                   savedInstanceState: Bundle
                   ) View
    - createSwipeCallback() ItemTouchHelper.SimpleCallback
    + onDestroyView() void
}

class EventsEntrantsFragment {
    <<fragment>>
    - FragmentEntrantsEventsBinding binding
    - EventArrayAdapter adapter
    - ListenerRegistration registrationListener
    - SessionManager SM
    - EventManager EM
    - UserInterfaceManager UIM
    - ListView listView
    - boolean historyMode
    - List<Event> allEventsLive
    - Set<String> joinedEventIdsLive
    - Map<Event, String> dayTypeByEvent
    - Map<Event, String> categoryByEvent
    - Map<Event, Long> timeByEvent
    - Map<String, String> registrationStatusByEventId
    - Status currentStatus
    - Set<String> selectedDayTypes
    - Set<String> selectedCategories
    - TextView emptyView

    + onCreate(savedInstanceState: Bundle) void
    + onCreateView(
                   inflater: LayoutInflater,
                   container: ViewGroup,
                   savedInstanceState: Bundle
                   ): 
                   View
    - clearAllFilters(): void
    - assignSidecarTagsForRealEvents(events: List<Event>): void
    - renderWithFilters(listView: ListView, actor: Actor): void
    - matchesStatus(e: Event): boolean
    - matchesAvailability(e: Event): boolean
    - matchesCategory(e: Event): boolean
    - attachEmptyViewToList(initialText: String): void
    + onDestroyView(): void
}

    class Status{
        <<enum>>
        ALL
        JOINED
        WAITLISTED
        SELECTED
        NOT_SELECTED
    }

    class ImagesAdapter {
    <<adapter>>
    - List<Event> events

    + ImagesAdapter()
    + onCreateViewHolder(parent: ViewGroup, viewType: int): ImagesViewHolder
    + onBindViewHolder(holder: ImagesViewHolder, position: int): void
    + getItemCount(): int
    + submitList(list: List<Event>): void
    + removeImage(position: int): void
    + getEventAt(position: int): Event
}

class ImagesViewHolder {
    - ImageView image
    - TextView title

    + ImagesViewHolder(itemView: View)
}


class ImagesFragment {
    <<fragment>>
    - FragmentAdministratorsImagesBinding binding
    - SessionManager SM
    - EventManager EM
    - ImagesAdapter adapter

    + ImagesFragment()
    + onCreate(savedInstanceState: Bundle): void
    + onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
    - createSwipeCallback(): ItemTouchHelper.SimpleCallback
}

class map {
    <<fragment>>
    - FrameLayout maps

    + map()
    + onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View
}

class NotificationsArrayAdapter {
    <<Adapter>>
    - IDatabase db
    - Actor actor
    - SessionManager SM
    - EventManager EM

    + NotificationsArrayAdapter(
        context: Context,
        events: ArrayList~Notifications~,
        db: IDatabase,
        actor: Actor
      )
    + getView(
        position: int,
        convertView: View?,
        parent: ViewGroup
      ): View
}

class NotificationsAdminArrayAdapter {
    <<Adapter>>
    - List~Notifications~ notifications

    + NotificationsAdminArrayAdapter(context: Context, notifications: List~Notifications~)
    + getView(position: int, convertView: View?, parent: ViewGroup): View
}

class NotificationsFragment {
    <<fragment>>
    - FragmentNotificationsBinding binding
    - SessionManager SM
    - NotificationManager NM

    + onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ) View

    + onDestroyView() void
}

class NotificationsFragmentAdmin {
    <<fragment>>
    - SessionManager SM
    - FragmentAdministratorsNotificationBinding binding

    + onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View
}

class SendNotificationsAdapter {
    <<Adapter>>
    - LayoutInflater inflater

    + SendNotificationsAdapter(context: Context, tags: List~String~)
    + getView(
          position: int,
          convertView: View?,
          parent: ViewGroup
      ): View
}

class SendNotificationsFragment {
    <<fragment>>
    - FragmentSendNotificationsBinding binding
    - FlexboxLayout flexboxLayout
    - AutoCompleteTextView autoCompleteTextView
    - List~String~ statuses
    - ArrayList~String~ recipientsOptions
    - ArrayList~String~ selectedPersons
    - SendNotificationsAdapter adapter
    - TextInputLayout messageLayout
    - ImageView closeButton
    - SessionManager SM
    - Button sendButton

    + onCreateDialog(savedInstanceState: Bundle?): Dialog
    - countChips(): int
}

class PickerFragment {
    <<fragment>>
    - ImageView imageView
    - TextView descriptionView
    - TextView placeholderDescription
    - TextView spotsFilled
    - TextView listInfo
    - TextView pick
    - ListView listView
    - Button btnPending
    - Button btnDeclined
    - Button btnWaiting
    - Button btnRejectedWaitlist
    - Button btnAccepted
    - Button btnWaitlist
    - EditText numberToPick
    - Button selectButton
    - Button backButton

    - RegistrationAdapter registrationAdapter
    - List~Registration~ registrationList
    - List~Registration~ waitingList

    - Event event
    - EventManager EM
    - NotificationManager NM

    + onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View
    - loadList(status: String): void
    - updateListInfo(status: String): void
}

class RegistrationAdapter {
    <<Adapter>>
    - OnDeleteListener listener

    + RegistrationAdapter(
          context: Context,
          registrations: List~Registration~,
          listener: OnDeleteListener?
      )
    + getView(
          position: int,
          convertView: View?,
          parent: ViewGroup
      ): View
}

class OnDeleteListener {
    <<interface>>
    + onDelete(status: String): void
}

class ProfileFragment {
    <<fragment>>
    - FragmentProfileBinding binding
    - ProfileViewModel profileViewModel

    - ActorManager AM
    - SessionManager SM
    - UserInterfaceManager UIM
    - NavigationManager NaM
    - NotificationManager NM
    - IDatabase db
    - Session session

    + onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View
    + onViewCreated(view: View, savedInstanceState: Bundle?): void
    - bindActorCard(a: Actor): void
    - showInlineEditDialog(): void
    - performProfileSave(
          name: String,
          email: String,
          phone: String,
          role: String,
          notificationsPreference: Boolean
      ): void
    - hasAllowedDomain(email: String): boolean
    - showDeleteDialog(): void
    + onDestroyView(): void
}
class QRCodeFragment {
    <<fragment>>
    - Button scanButton
    - SessionManager SM
    - UserInterfaceManager UIM
    - NavigationManager NM
    - EventManager EM

    + onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View
    - startQrScanner(): void
    + onActivityResult(
          requestCode: int,
          resultCode: int,
          data: Intent?
      ): void
}

class ProfileViewModel {
    <<ViewModel>>
    - MutableLiveData~String~ mText
    - MutableLiveData~Actor~ actor

    + ProfileViewModel()
    + getText(): LiveData~String~
    + getActor(): LiveData~Actor~
    + setActor(a: Actor): void
    + updateActor(
          name: String,
          email: String,
          phone: String,
          notificationsPreference: Boolean
      ): void
}

class QRCodeViewModel {
    <<ViewModel>>
    - MutableLiveData~String~ mText

    + QRCodeViewModel()
    + getText(): LiveData~String~
}

class UsersAdapter {
    <<Adapter>>
    - List~Actor~ actors

    + UsersAdapter()
    + onCreateViewHolder(parent: ViewGroup, viewType: int): UserViewHolder
    + onBindViewHolder(holder: UserViewHolder, position: int): void
    + getItemCount(): int
    + submitList(list: List~Actor~): void
    + getActorAt(position: int): Actor
    + removeAt(position: int): void
}

class UserViewHolder {
    <<ViewHolder>>
    - ImageView profile
    - TextView name
    - TextView role

    + UserViewHolder(itemView: View)
}

UsersFragment --|> Fragment

UsersFragment --> FragmentAdministratorsUsersBinding
UsersFragment --> SessionManager
UsersFragment --> ActorManager
UsersFragment --> UsersAdapter
UsersFragment --> Actor
UsersFragment --> RecyclerView
UsersFragment --> ItemTouchHelper

class FoundationActivity {
    <<activity>>
    # SessionManager SM
    # NavigationManager NM

    + onCreate(savedInstanceState: Bundle): void
    + onStart(): void
}

class MainActivity {
    <<activity>>
    - SessionManager SM
    - UserInterfaceManager UIM
    - NavigationManager NM
    - ActorManager AM
    - String role
    - ActivityMainBinding binding

    + onCreate(savedInstanceState: Bundle): void
    + onSupportNavigateUp(): boolean
}
SignupActivity --|> FoundationActivity

SignupActivity --> SessionManager
SignupActivity --> UserInterfaceManager
SignupActivity --> NavigationManager
SignupActivity --> ActorManager
SignupActivity --> Actor
SignupActivity --> Entrant
SignupActivity --> Organizer
SignupActivity --> Administrator
SignupActivity --> MainActivity


CreateFragment --|> Fragment

CreateFragment --> CreateViewModel
CreateFragment --> EventManager
CreateFragment --> SessionManager
CreateFragment --> EventsApp
CreateFragment --> Event
CreateFragment --> NavController
CreateFragment --> NavOptions

CreateViewModel --|> ViewModel


EditFragment --|> Fragment

EditFragment --> EventManager
EditFragment --> EditViewModel
EditFragment --> SessionManager
EditFragment --> EventsApp
EditFragment --> Event
EditFragment --> Registration
EditFragment --> SendNotificationsFragment
EditFragment --> NavController
EditFragment --> NavOptions

EditViewModel --|> ViewModel

EditViewModel --> LiveData
EditViewModel --> MutableLiveData


AdministratorsEventsAdapter --> Event
AdministratorsEventsAdapter --> AdminEventViewHolder
AdminEventViewHolder --> ImageView
AdminEventViewHolder --> TextView


EventArrayAdapter --|> ArrayAdapter~Event~

EventArrayAdapter --> Event
EventArrayAdapter --> Actor
EventArrayAdapter --> SessionManager
EventArrayAdapter --> EventManager
EventArrayAdapter --> ActorManager
EventArrayAdapter --> NavController
EventArrayAdapter --> Session
EventArrayAdapter --> IDatabase
EventArrayAdapter --> MaterialButton

EventInformationActivity --|> AppCompatActivity
EventInformationActivity --> SessionManager
EventInformationActivity --> UserInterfaceManager
EventInformationActivity --> EventManager
EventInformationActivity --> Event


EventsAdministratorsFragment --|> Fragment

EventsAdministratorsFragment --> EventsApp
EventsAdministratorsFragment --> SessionManager
EventsAdministratorsFragment --> EventManager
EventsAdministratorsFragment --> AdministratorsEventsAdapter
EventsAdministratorsFragment --> Event
EventsAdministratorsFragment --> RecyclerView
EventsAdministratorsFragment --> ItemTouchHelper

EventsEntrantsFragment --|> Fragment

EventsEntrantsFragment --> FragmentEntrantsEventsBinding
EventsEntrantsFragment --> EventArrayAdapter
EventsEntrantsFragment --> SessionManager
EventsEntrantsFragment --> EventManager
EventsEntrantsFragment --> UserInterfaceManager
EventsEntrantsFragment --> Actor
EventsEntrantsFragment --> Event
EventsEntrantsFragment --> Registration
EventsEntrantsFragment --> ListenerRegistration
EventsEntrantsFragment --> ListView
EventsEntrantsFragment --> NavController
EventsEntrantsFragment --> ChipGroup
EventsEntrantsFragment --> FloatingActionButton
EventsEntrantsFragment --> Status


ImagesAdapter --|> RecyclerView.Adapter
ImagesAdapter --> ImagesViewHolder
ImagesAdapter --> Event
ImagesAdapter --> Bitmap
ImagesAdapter --> Base64
ImagesViewHolder --|> RecyclerView.ViewHolder



ImagesFragment --|> Fragment
ImagesFragment --> SessionManager
ImagesFragment --> EventManager
ImagesFragment --> ImagesAdapter
ImagesFragment --> Event
ImagesFragment --> RecyclerView
ImagesFragment --> ItemTouchHelper



map --|> Fragment

map --> SessionManager
map --> EventManager
map --> ActorManager
map --> Event
map --> Registration
map --> SupportMapFragment
map --> LatLng
map --> Marker
map --> MarkerOptions


NotificationsAdminArrayAdapter --|> ArrayAdapter~Notifications~
NotificationsAdminArrayAdapter --> Notifications
NotificationsAdminArrayAdapter --> MaterialButton
NotificationsAdminArrayAdapter --> ImageView
NotificationsAdminArrayAdapter --> TextView


NotificationsArrayAdapter --|> ArrayAdapter~Notifications~

NotificationsArrayAdapter --> Notifications
NotificationsArrayAdapter --> IDatabase
NotificationsArrayAdapter --> Actor
NotificationsArrayAdapter --> SessionManager
NotificationsArrayAdapter --> EventManager

NotificationsArrayAdapter --> MaterialButton
NotificationsArrayAdapter --> MaterialButtonToggleGroup
NotificationsArrayAdapter --> ImageView
NotificationsArrayAdapter --> TextView
NotificationsArrayAdapter --> Snackbar
NotificationsArrayAdapter --> ShapeAppearanceModel
NotificationsArrayAdapter --> EventsApp


NotificationsFragment --|> Fragment

NotificationsFragment --> FragmentNotificationsBinding
NotificationsFragment --> SessionManager
NotificationsFragment --> NotificationManager
NotificationsFragment --> NotificationsArrayAdapter
NotificationsFragment --> Notifications
NotificationsFragment --> Actor
NotificationsFragment --> IDatabase
NotificationsFragment --> EventsApp
NotificationsFragment --> ListView
NotificationsFragment --> Session


NotificationsFragmentAdmin --|> Fragment

NotificationsFragmentAdmin --> SessionManager
NotificationsFragmentAdmin --> EventsApp
NotificationsFragmentAdmin --> IDatabase
NotificationsFragmentAdmin --> FragmentAdministratorsNotificationBinding
NotificationsFragmentAdmin --> NotificationsAdminArrayAdapter
NotificationsFragmentAdmin --> Notifications
NotificationsFragmentAdmin --> ListView

SendNotificationsAdapter --|> ArrayAdapter~String~

SendNotificationsAdapter --> LayoutInflater
SendNotificationsAdapter --> View
SendNotificationsAdapter --> TextView

SendNotificationsFragment --|> DialogFragment

SendNotificationsFragment --> FragmentSendNotificationsBinding
SendNotificationsFragment --> SessionManager
SendNotificationsFragment --> EventsApp
SendNotificationsFragment --> Session
SendNotificationsFragment --> IDatabase
SendNotificationsFragment --> Actor
SendNotificationsFragment --> UserInterfaceManager
SendNotificationsFragment --> Event
SendNotificationsFragment --> NotificationManager
SendNotificationsFragment --> SendNotificationsAdapter

SendNotificationsFragment --> FlexboxLayout
SendNotificationsFragment --> AutoCompleteTextView
SendNotificationsFragment --> Chip
SendNotificationsFragment --> TextInputLayout
SendNotificationsFragment --> Button
SendNotificationsFragment --> ImageView
SendNotificationsFragment --> Snackbar
SendNotificationsFragment --> AlertDialog

PickerFragment --|> Fragment

PickerFragment --> EventsApp
PickerFragment --> SessionManager
PickerFragment --> EventManager
PickerFragment --> NotificationManager
PickerFragment --> Event
PickerFragment --> Registration
PickerFragment --> RegistrationAdapter
PickerFragment --> IDatabase
PickerFragment --> Actor

PickerFragment --> ImageView
PickerFragment --> TextView
PickerFragment --> ListView
PickerFragment --> Button
PickerFragment --> EditText
PickerFragment --> NavController
PickerFragment --> NavHostFragment

RegistrationAdapter --|> ArrayAdapter~Registration~
RegistrationAdapter --> Registration
RegistrationAdapter --> EventsApp
RegistrationAdapter --> SessionManager
RegistrationAdapter --> EventManager
RegistrationAdapter --> Event
RegistrationAdapter --> TextView
RegistrationAdapter --> ImageButton
RegistrationAdapter --> Toast
RegistrationAdapter --> OnDeleteListener

ProfileFragment --|> Fragment

ProfileFragment --> FragmentProfileBinding
ProfileFragment --> ProfileViewModel
ProfileFragment --> Actor
ProfileFragment --> ActorManager
ProfileFragment --> SessionManager
ProfileFragment --> UserInterfaceManager
ProfileFragment --> NavigationManager
ProfileFragment --> NotificationManager
ProfileFragment --> IDatabase
ProfileFragment --> Session
ProfileFragment --> SignupActivity

ProfileViewModel --|> ViewModel
ProfileViewModel --> Actor
ProfileViewModel --> LiveData
ProfileViewModel --> MutableLiveData


QRCodeFragment --|> Fragment

QRCodeFragment --> SessionManager
QRCodeFragment --> UserInterfaceManager
QRCodeFragment --> NavigationManager
QRCodeFragment --> EventManager
QRCodeFragment --> EventInformationActivity

QRCodeFragment --> IntentIntegrator
QRCodeFragment --> IntentResult
QRCodeFragment --> CaptureActivity
QRCodeFragment --> Button


QRCodeViewModel --|> ViewModel
QRCodeViewModel --> LiveData
QRCodeViewModel --> MutableLiveData


UsersAdapter --|> RecyclerView.Adapter
UsersAdapter --> UserViewHolder
UsersAdapter --> Actor

UserViewHolder --|> RecyclerView.ViewHolder
UserViewHolder --> ImageView
UserViewHolder --> TextView


UsersFragment --|> Fragment

UsersFragment --> FragmentAdministratorsUsersBinding
UsersFragment --> SessionManager
UsersFragment --> ActorManager
UsersFragment --> UsersAdapter
UsersFragment --> Actor
UsersFragment --> RecyclerView
UsersFragment --> ItemTouchHelper

FoundationActivity --|> AppCompatActivity
FoundationActivity --> SessionManager
FoundationActivity --> NavigationManager
FoundationActivity --> EventsApp

MainActivity --|> FoundationActivity
MainActivity --> SessionManager
MainActivity --> UserInterfaceManager
MainActivity --> NavigationManager
MainActivity --> ActorManager
MainActivity --> Actor
MainActivity --> SignupActivity
