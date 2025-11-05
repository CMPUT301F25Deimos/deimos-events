package com.example.deimos_events.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class EventsViewModel extends ViewModel {
    
    private final MutableLiveData<String> event;
    
    public EventsViewModel() {
        event = new MutableLiveData<>();
    }
    
    public LiveData<String> getEvent() {
        return event;
    }
}
