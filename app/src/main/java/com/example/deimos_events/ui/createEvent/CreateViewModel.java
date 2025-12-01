package com.example.deimos_events.ui.createEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for the create-event screen.
 * Holds observable UI text such as status messages or validation feedback.
 * This class can be extended to store additional event-creation data.
 */
public class CreateViewModel extends ViewModel {

    /** LiveData holding the current message for the create-event UI. */
    private final MutableLiveData<String> message = new MutableLiveData<>();

    /**
     * Returns observable message data for the UI.
     * @return a {@link LiveData} representing the current message
     */
    public LiveData<String> getMessage() {
        return message;
    }
    /**
     * Updates the message shown in the create-event UI.
     * @param newMessage the new message value
     */
    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
}
