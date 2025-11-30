package com.example.deimos_events.managers;


import com.example.deimos_events.Database;
import com.example.deimos_events.Session;
import com.example.deimos_events.dataclasses.Actor;

/**
 * Top-level manager that gives access to the {@link Session} Object
 * <p>
 *
 * All lower level managers that require access to the session Object must do it through this class
 * The {@code SessionManager} is responsible for instantiating and keeping track of all lower-level
 * managers, and setting up the the database connection.
 *
 * <p>
 * A single instance of this class is created in {@link com.example.deimos_events.EventsApp} which allows for
 * it to be shared between all activities. This gives all activities access to session state, navigation
 * and database control through the corresponding managers such as {@link ActorManager}.
 */
public class SessionManager {



    private  ActorManager actorManager;
    private  EventManager eventManager;
    private  UserInterfaceManager userInterfaceManager;
    private  NotificationManager notificationManager;

    private NavigationManager navigationManager;

    private  InvitationManager invitationManager;

    private  Session session;
    private final boolean fullInitialization;


    public SessionManager() {
        this(true);
    }

    /**
     * Creates a session manager and when {@code fullInitialization} is true,
     * creates all lower-level managers, the {@link Session} instance and a new {@link Database} connection.
     * This is the mode used during normal application processes
     * <p>
     * When {@code fullInitialization} is false each manager and the session instance must be manually created.
     * This is used for testing.
     * @param fullInitialization a boolean that determine whether or not all session managers will be initialized
     */
    public SessionManager(boolean fullInitialization){
        this.fullInitialization = fullInitialization;
        if (fullInitialization) {
            this.actorManager = new ActorManager(this);
            this.eventManager = new EventManager(this);
            this.userInterfaceManager = new UserInterfaceManager(this);
            this.navigationManager = new NavigationManager(this);
            this.invitationManager = new InvitationManager(this);
            this.notificationManager = new NotificationManager(this);
            this.session = new Session(new Database());
        }
    }


    public InvitationManager getInvitationManager() {
        return checkObjectInitialized("InvitationManager", invitationManager);
    }

    public NotificationManager getNotificationManager() {
        return checkObjectInitialized("NotificationManager",notificationManager);
    }

    /**
     * Returns the initialized {@link UserInterfaceManager}.
     * This manager is created during {@link SessionManager} creation.
     * @return the initialized UserInterfaceManager.
     * @see UserInterfaceManager
     */
    public UserInterfaceManager getUserInterfaceManager() {
        return checkObjectInitialized("UserInterfaceManager", userInterfaceManager);
    }

    /**
     * Returns the initialized {@link NavigationManager}.
     * <p>
     * Provided for activities and fragments to perform app-level navigation.
     * @return the initialized UserInterfaceManager.
     * @see NavigationManager
     */
    public NavigationManager getNavigationManager() {
        return checkObjectInitialized("NavigationManager", navigationManager);
    }

    /**
     * Returns the applications current {@link Session} instance.
     * <p>
     * The session contains the shared state and provides access to the database connection.
     *
     * @return Session Instance
     * @see Session
     */
    public Session getSession() {
        return checkObjectInitialized("Session", session);
    }

    public EventManager getEventManager() {
         return checkObjectInitialized("EventManager", eventManager);
    }
    /**
     * Returns the initialized {@link ActorManager}.
     * <p>
     * All operations on actor objects should be accessed through this shared instance.
     *
     * @return the initialized ActorManager.
     * @see ActorManager
     */
    public ActorManager getActorManager() {
        return checkObjectInitialized("ActorManager", actorManager);
    }

    public void setSession(Session session) {
        checkFullInitialization(this);
        this.session = session;
    }
    private void checkFullInitialization(Object obj){
        if (fullInitialization){
            String msg = obj.getClass().getSimpleName() + " has already been initialized";
            throw new IllegalStateException(msg);
        }
    }

    private <T> T checkObjectInitialized(String name, T value){
        if (value == null){
            throw new IllegalStateException(name + " has not been initalized yet");
        }
        return value;
    }


    public boolean isFullInitialization(){
        return fullInitialization;
    }
}
