package com.example.deimos_events.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Roles;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<Actor> actor = new MutableLiveData<>();

    public ProfileViewModel() {
        mText.setValue("Profile");
        actor.setValue(new Actor("tempUserId", "Alex Entrant", "alex.entrant@example.com", "780-555-1212", Roles.ENTRANT));
    }

    public LiveData<String> getText() { return mText; }
    public LiveData<Actor> getActor() { return actor; }

    public void setActor(Actor a) { actor.setValue(a); }

    public void updateActor(String name, String email, String phone) {
        Actor cur = actor.getValue();
        String uid = (cur == null) ? "tempUserId" : cur.getDeviceIdentifier();
        String role = (cur == null) ? Roles.ENTRANT : cur.getRole();
        actor.setValue(new Actor(uid, name, email, phone, role));
    }
}
