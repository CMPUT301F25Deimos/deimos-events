package com.example.deimos_events.managers;

import com.example.deimos_events.Invitation;
import com.example.deimos_events.Session;

/**
 * Handles persistent operations connected to the {@link Invitation} instances
 * such as deleting, posting, or retrieving data from the database.
 * <p>
 * activities may instantiate {@link Invitation} objects temporarily, but all persistent operations
 * must be performed via the {@code InvitationManager}.
 *<p>
 * data retrieved from the database is stored in the {@link Session} for use by other classes.
 */
public class InvitationManager {
    private final SessionManager sessionManager;

    public InvitationManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
}
