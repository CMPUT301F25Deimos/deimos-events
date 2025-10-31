package com.example.deimos_events;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class ActorManager {

    private final SessionManager sessionManager;

    public ActorManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

    }

    public void deleteActor() {
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        db.deleteActor(actor, success -> {
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE, "Succeeded on Deleting User");
                sessionManager.setCurrentActor();
            } else {
                r = new Result(Boolean.FALSE, "Failed to delete user");
            }
            sessionManager.setResult(r);
        });
    }

    public void createActor(Context context, String name, String email, String phoneNo) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        Session session = sessionManager.getSession();
        Database db = session.getDatabase();
        Actor actor = new Actor(androidId, name, email, phoneNo);
        db.createActor(actor, success -> {
            Result r;
            if (success) {
                r = new Result(Boolean.TRUE, "Succeeded on creating User");
                sessionManager.setCurrentActor(actor);
            } else {
                r = new Result(Boolean.FALSE, "Failed to create user");
            }
            sessionManager.setResult(r);
        });
    }
}
