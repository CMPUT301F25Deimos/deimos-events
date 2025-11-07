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
    @Nullable private String role;
    public Actor (String deviceIdentifier, String name, String email, String phoneNumber, @Nullable String role) {
        this.deviceIdentifier = deviceIdentifier;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public Actor (String deviceIdentifier, String name, String email, String phoneNumber) {
       this(deviceIdentifier, name, email, phoneNumber, null);
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
}