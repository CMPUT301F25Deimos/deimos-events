package com.example.deimos_events;

import androidx.annotation.Nullable;

/**
 * An abstract class for all types of actors to inherit properties and methods from.
 */
public  class Actor {
    private String deviceIdentifier;
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean notificationsPreference;
    @Nullable private String role;
    public Actor (String deviceIdentifier, String name, String email, String phoneNumber, @Nullable String role, Boolean notificationsPreference) {
        this.deviceIdentifier = deviceIdentifier;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.notificationsPreference = notificationsPreference;
    }

    public Actor (String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
       this(deviceIdentifier, name, email, phoneNumber, null, true);
    }


    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getRole(){
        return role;
    }
    
    public Boolean getNotificationsPreference() {
        return notificationsPreference;
    }
}