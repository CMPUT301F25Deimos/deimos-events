package com.example.deimos_events;


/**
 * Top-level manager that gives access to the {@link Session} Object
 * <p>
 *
 * All lower level managers that require access to the session Object must do it through this class
 * The {@code SessionManager} is responsible for instantiating and keeping track of all lower-level
 * managers, and setting up the the database connection.
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

    public UserInterfaceManager getUserInterfaceManager() {
        return checkObjectInitialized("UserInterfaceManager", userInterfaceManager);
    }

    public NavigationManager getNavigationManager() {
        return checkObjectInitialized("NavigationManager", navigationManager);
    }

    public Session getSession() {
        return checkObjectInitialized("Session", session);
    }

    public EventManager getEventManager() {
         return checkObjectInitialized("EventManager", eventManager);
    }

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
