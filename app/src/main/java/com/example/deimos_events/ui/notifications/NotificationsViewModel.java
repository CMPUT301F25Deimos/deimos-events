package com.example.deimos_events.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * helps for Notifications Fragment UI
 */
public class NotificationsViewModel extends ViewModel {
    
    private final MutableLiveData<String> mText;
    
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }
    
    public LiveData<String> getText() {
        return mText;
    }
}