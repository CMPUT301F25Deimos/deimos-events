package com.example.deimos_events;

public class SessionManager {



    private final ActorManager actorManager;
    private final EventManager eventManager;
    private final UserInterfaceManager userInterfaceManager;
    private final NotificationManager notificationManager;

    private final InvitationManager invitationManager;

    private final Session session;


    public SessionManager(){
        this.actorManager = new ActorManager(this);
        this.eventManager = new EventManager(this);
        this.userInterfaceManager = new UserInterfaceManager(this);
        this.invitationManager = new InvitationManager(this);
        this.notificationManager = new NotificationManager(this);
        this.session = new Session(new Database());
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

    public void setResult(){
        this.session.setResult();
    }

    public void setResult(Result result){
        this.session.setResult(result);
    }

    public void setCurrentActor(){
        this.session.setCurrentActor();
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




}
