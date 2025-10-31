package com.example.deimos_events.ui.qr_code;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRCodeViewModel extends ViewModel {
    
    private final MutableLiveData<String> mText;
    
    public QRCodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is QR code fragment");
    }
    
    public LiveData<String> getText() {
        return mText;
    }
}