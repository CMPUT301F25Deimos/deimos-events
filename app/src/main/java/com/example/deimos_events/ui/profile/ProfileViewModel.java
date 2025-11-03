package com.example.deimos_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    // Old text LiveData (kept so your existing binding.textProfile still works)
    private final MutableLiveData<String> mText = new MutableLiveData<>();

    // Real profile data
    private final MutableLiveData<Profile> profile = new MutableLiveData<>();

    public ProfileViewModel() {
        mText.setValue("Profile"); // header text if you want to show it
        // Temporary local data until Firebase is plugged in
        profile.setValue(new Profile("tempUserId", "Alex Entrant", "alex.entrant@example.com", "780-555-1212"));
    }

    public LiveData<String> getText() { return mText; }
    public LiveData<Profile> getProfile() { return profile; }

    public void updateProfile(String name, String email, String phone) {
        Profile cur = profile.getValue();
        String uid = (cur == null) ? "tempUserId" : cur.getUserId();
        profile.setValue(new Profile(uid, name, email, phone));
        // Later: replace with a call to Firebase to persist changes
    }
}
