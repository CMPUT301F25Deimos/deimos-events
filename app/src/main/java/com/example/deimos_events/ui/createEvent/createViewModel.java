package com.example.deimos_events.ui.createEvent;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class createViewModel extends ViewModel {

    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<String> getMessage() {
        return message;
    }

    public void updateMessage(String newMessage) {
        message.setValue(newMessage);
    }
}
