package com.example.deimos_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<Profile> profile = new MutableLiveData<>();

    public ProfileViewModel() {
        mText.setValue("Profile");
        profile.setValue(new Profile("tempUserId", "Alex Entrant", "alex.entrant@example.com", "780-555-1212"));
    }

    public LiveData<String> getText() { return mText; }
    public LiveData<Profile> getProfile() { return profile; }

    public void setProfile(Profile p) { profile.setValue(p); }

    public void updateProfile(String name, String email, String phone) {
        Profile cur = profile.getValue();
        String uid = (cur == null) ? "tempUserId" : cur.getUserId();
        profile.setValue(new Profile(uid, name, email, phone));
    }
}
