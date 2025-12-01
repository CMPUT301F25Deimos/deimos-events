package com.example.deimos_events.ui.createEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the create-event screen.
 * <p>
 * uses only a single observable message field that can be used by the UI to show
 * status text or validation feedback.
 * </p>
 * This class can be extended to hold additional data like title, date, or capacity.
 */
public class CreateViewModel extends ViewModel {

    /**
     * Livedata holding the current message for the create-event UI.
     */
    private final MutableLiveData<String> message = new MutableLiveData<>();

    /**
     *
     * @return a {@link LiveData} representing the current message
     */
    public LiveData<String> getMessage() {
        return message;
    }

    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
}
