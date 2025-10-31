package com.example.deimos_events;

import java.util.function.Consumer;

public class ActorManager {

    private final SessionManager sessionManager;
    public ActorManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;

    }


    public void deleteActor(){
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        db.deleteActor(actor, success -> {
            Result r;
            if (success){
                 r = new Result(Boolean.TRUE, "Succeeded on Deleting User");
            }else{
                 r = new Result(Boolean.FALSE, "Failed to delete user");
            }
            // Now ask the SessionManager to update
            sessionManager.updateSession(r);
        });
    }





}
