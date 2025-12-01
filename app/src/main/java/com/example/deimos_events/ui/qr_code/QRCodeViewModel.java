package com.example.deimos_events.ui.qr_code;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
/**
 * ViewModel for the QR code fragment.
 * <p>
 * Provides observable text data used by the UI to display
 * static or dynamic information related to the QR scanning screen.
 */
public class QRCodeViewModel extends ViewModel {
    /** LiveData holding the display text for the QR code UI. */
    private final MutableLiveData<String> mText;
    /**
     * Initializes the ViewModel and sets the default text
     * shown on the QR code fragment.
     */
    public QRCodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is QR code fragment");
    }
    /**
     * Returns the observable text value for the QR code UI.
     *
     * @return LiveData containing the text displayed in the fragment.
     */
    public LiveData<String> getText() {
        return mText;
    }
}