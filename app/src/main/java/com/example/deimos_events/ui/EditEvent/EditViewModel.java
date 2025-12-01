package com.example.deimos_events.ui.EditEvent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
/**
 * ViewModel used by the Edit Event screen to store and manage UI-related data.
 *
 * <p>This ViewModel exposes a {@link LiveData} message that can be observed
 * by the fragment to react to changes without needing to manage its own
 * lifecycle-bound storage.</p>
 *
 * <p>Typical use cases include displaying status messages, updating labels,
 * or coordinating simple UI state changes across configuration changes.</p>
 */
public class EditViewModel  extends ViewModel {

    /**
     * Backing field for the observable message.
     * <p>
     * {@link MutableLiveData} allows internal modification, while
     * external consumers receive the exposed {@link LiveData} version.
     * </p>
     */
    private final MutableLiveData<String> message = new MutableLiveData<>();
    /**
     * Returns an observable {@link LiveData} stream containing the
     * current message value.
     *
     * <p>Fragments can observe this value to automatically update
     * UI components when the message changes.</p>
     *
     * @return a {@link LiveData} representing the current message
     */
    public LiveData<String> getMessage() {
        return message;
    }
    /**
     * Updates the current message value.
     *
     * <p>This triggers UI updates for any active observers
     * of {@link #getMessage()}.</p>
     *
     * @param newMessage the new message to store and publish
     */
    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
}
