package com.example.deimos_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Roles;
/**
 * ViewModel for the Profile screen.
 * <p>
 * Exposes:
 * <ul>
 *   <li>A title text for the toolbar/header.</li>
 *   <li>The current {@link Actor} shown in the profile card.</li>
 * </ul>
 * The ViewModel provides simple setters to update in-memory UI state; persistence and
 * server-side changes are handled by managers outside the ViewModel.
 */
public class ProfileViewModel extends ViewModel {

    /** Observable title text displayed in the Profile UI. */
    private final MutableLiveData<String> mText = new MutableLiveData<>();
    /** Observable current actor displayed in the profile card. */
    private final MutableLiveData<Actor> actor = new MutableLiveData<>();

    /** Initializes default UI state (title and a placeholder actor). */
    public ProfileViewModel() {
        mText.setValue("Profile");
        actor.setValue(new Actor("tempUserId", "Alex Entrant", "alex.entrant@example.com", "780-555-1212", Roles.ENTRANT, true));
    }

    /** @return live title text for the Profile screen */
    public LiveData<String> getText() { return mText; }
    /** @return live {@link Actor} used by the UI */
    public LiveData<Actor> getActor() { return actor; }

    /**
     * Replaces the current actor emitted by this ViewModel.
     *
     * @param a the new actor to display (may be {@code null})
     */
    public void setActor(Actor a) { actor.setValue(a); }

    /**
     * Convenience method to update a subset of the actor fields in-place for UI refresh.
     * <p>
     * This does not persist to storage; callers should perform remote/local persistence
     * through appropriate managers before invoking this update for display.
     *
     * @param name  new display name
     * @param email new email address
     * @param phone new phone number (may be empty)
     */
    public void updateActor(String name, String email, String phone, Boolean notificationsPreference) {
        Actor cur = actor.getValue();
        String uid = (cur == null) ? "tempUserId" : cur.getDeviceIdentifier();
        String role = (cur == null) ? Roles.ENTRANT : cur.getRole();
        actor.setValue(new Actor(uid, name, email, phone, role, notificationsPreference));
    }
}
