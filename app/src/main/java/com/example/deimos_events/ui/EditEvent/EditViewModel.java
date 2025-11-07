package com.example.deimos_events.ui.EditEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditViewModel  extends ViewModel {
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<String> getMessage() {
        return message;
    }

    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
}
