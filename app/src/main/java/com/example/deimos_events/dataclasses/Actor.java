package com.example.deimos_events.dataclasses;
import androidx.annotation.Nullable;
/**
 * Represents a generic actor in the system.
 * <p>
 * This abstract base-like class stores common properties for all user types,
 * including device identifier, contact information, role, and notification preferences.
 * All specific actor types (e.g., Entrant, Organizer, Administrator) should inherit from this class.
 * </p>
 */
public class Actor {
    /** The unique device identifier associated with this actor. */
    private String deviceIdentifier;
    /** The actor's display name. */
    private String name;
    /** The actor's email address. */
    private String email;
    /** The actor's phone number. */
    private String phoneNumber;
    /** Whether this actor has enabled notifications. */
    private Boolean notificationsPreference;
    /** The role of the actor (e.g., "Entrant", "Organizer", "Admin"). */
    @Nullable private String role;
    /**
     * Constructs a new Actor with all properties explicitly provided.
     *
     * @param deviceIdentifier the unique device identifier for the actor
     * @param name the actor's name
     * @param email the actor's email address
     * @param phoneNumber the actor's phone number
     * @param role the role of the actor (nullable)
     * @param notificationsPreference whether the actor has enabled notifications
     */
    public Actor (String deviceIdentifier, String name, String email, String phoneNumber, @Nullable String role, Boolean notificationsPreference) {
        this.deviceIdentifier = deviceIdentifier;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.notificationsPreference = notificationsPreference;
    }
    /**
     * Default empty constructor used for serialization/deserialization frameworks.
     */
    public Actor(){}
    /**
     * Constructs a new Actor without specifying a role.
     * Notifications preference defaults to {@code true}.
     *
     * @param deviceIdentifier the unique device identifier for the actor
     * @param name the actor's name
     * @param email the actor's email address
     * @param phoneNumber the actor's phone number
     * @param notificationsPreference whether the actor has enabled notifications
     */
    public Actor (String deviceIdentifier, String name, String email, String phoneNumber, Boolean notificationsPreference) {
        this(deviceIdentifier, name, email, phoneNumber, null, true);
    }
    /**
     * @return the unique device identifier of the actor
     */
    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
    /**
     * @return the actor's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * @return the actor's email address
     */
    public String getEmail() {
        return email;
    }
    /**
     * @return the actor's name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the role of the actor, or {@code null} if not assigned
     */
    public String getRole(){
        return role;
    }
    /**
     * @return {@code true} if the actor has notifications enabled; {@code false} otherwise
     */
    public Boolean getNotificationsPreference() {
        return notificationsPreference;
    }
}
