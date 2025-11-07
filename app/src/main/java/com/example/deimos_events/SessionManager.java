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
            this.invitationManager = new InvitationManager(this);
            this.notificationManager = new NotificationManager(this);
            this.session = new Session(new Database());
        }
    }


    public InvitationManager getInvitationManager() {
        return invitationManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public UserInterfaceManager getUserInterfaceManager() {
        return userInterfaceManager;
    }


    public Session getSession() {
        return session;
    }


    public void setResult(Result result){
        this.session.setResult(result);
    }


    public void setCurrentActor(Actor actor){
        this.session.setCurrentActor(actor);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ActorManager getActorManager() {
        return actorManager;
    }

    public void setSession(Session session) {
        isFullInitialized(this);
        this.session = session;
    }

    private void isFullInitialized(Object obj){
        if (fullInitialization){
            String msg = obj.getClass().getSimpleName() + "Has already been initialized";
            throw new IllegalStateException(msg);
        }
    }
}
