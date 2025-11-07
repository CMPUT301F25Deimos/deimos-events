package com.example.deimos_events.ui.profile;

/**
 * Immutable UI-facing representation of a user's profile details.
 * <p>
 * This class is used by the Profile screen to display the signed-in user's core
 * attributes (name, email, phone, and role). It does not perform any business logic
 * and intentionally exposes only getters to keep the view layer simple.
 */
public class Profile {
    /** Unique identifier for the user (e.g., device ID or actor ID). */
    private final String userId;
    /** Display name of the user. */
    private final String name;
    /** Email address of the user. */
    private final String email;
    /** Optional phone number for the user. */
    private final String phone;
    /** Role string to show in the UI (e.g., "Entrant", "Organizer"). */
    private final String role;

    /**
     * Constructs a {@code Profile} value object for the Profile UI.
     *
     * @param userId unique identifier of the user
     * @param name   display name
     * @param email  email address
     * @param phone  phone number (may be empty)
     * @param role   role label to display
     */
    public Profile(String userId, String name, String email, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    /** @return the unique user identifier */
    public String getUserId() { return userId; }
    /** @return the display name */
    public String getName() { return name; }
    /** @return the email address */
    public String getEmail() { return email; }
    /** @return the phone number (may be empty) */
    public String getPhone() { return phone; }
    /** @return the role label for display */
    public String getRole() {return role; }

}
