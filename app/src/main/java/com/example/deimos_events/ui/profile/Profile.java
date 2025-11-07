package com.example.deimos_events.ui.profile;

public class Profile {
    private final String userId;
    private final String name;
    private final String email;
    private final String phone;

    private final String role;

    public Profile(String userId, String name, String email, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() {return role; }

}
