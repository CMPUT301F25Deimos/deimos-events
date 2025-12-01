package com.example.deimos_events;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.managers.SessionManager;
/**
 * A base activity class that initializes shared application managers used across
 * multiple screens in the Deimos Events application.
 * <p>
 * {@code FoundationActivity} retrieves the {@link SessionManager} and
 * {@link NavigationManager} instances from the {@link EventsApp} and ensures
 * they remain synchronized with the activity lifecycle.
 * </p>
 *
 * <p>
 * Any activity extending this class automatically gains access to:
 * <ul>
 *     <li>The global {@link SessionManager} instance</li>
 *     <li>The associated {@link NavigationManager}</li>
 * </ul>
 * These managers are refreshed in both {@link #onCreate(Bundle)} and
 * {@link #onStart()} to guarantee the correct activity context is always set
 * for navigation.
 * </p>
 */
public abstract class FoundationActivity extends AppCompatActivity {
    /**
     * Manages session-level data including the logged-in {@code Actor},
     * cached database objects, and global state used by the app.
     */
    protected SessionManager SM;
    /**
     * Handles navigation between fragments and activities. The active
     * {@code FoundationActivity} is injected into the manager so that it
     * always navigates using the correct context.
     */
    protected NavigationManager NM;
    /**
     * Initializes the {@link SessionManager} and {@link NavigationManager}
     * when the activity is first created.
     *
     * @param savedInstance the saved activity state bundle, if any.
     */
    @Override
    protected  void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        NM = SM.getNavigationManager();
        NM.setActivity(this);
    }
    /**
     * Re-fetches and rebinds the global managers each time the activity becomes visible.
     * Ensures that {@link NavigationManager} always holds the current activity reference.
     */
    @Override
    protected void onStart(){
        super.onStart();
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        NM = SM.getNavigationManager();
        NM.setActivity(this);
    }
}

