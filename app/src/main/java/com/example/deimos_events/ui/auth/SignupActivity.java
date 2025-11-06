package com.example.deimos_events.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Administrator;
import com.example.deimos_events.Database;
import com.example.deimos_events.Entrant;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.MainActivity;
import com.example.deimos_events.NavigationManager;
import com.example.deimos_events.Organizer;
import com.example.deimos_events.R;
import com.example.deimos_events.Roles;
import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout      tilName, tilEmail, tilPhone, tilDeviceId, tilRole;
    private TextInputEditText    etDeviceId, etName, etEmail, etPhone;
    private AutoCompleteTextView etRole;
    private MaterialButton       btnSignup, btnMore;

    private IDatabase db;

    private SessionManager       SM;
    private UserInterfaceManager UIM;
    private NavigationManager    NM;

    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(
            Arrays.asList("gmail.com", "outlook.com", "hotmail.com", "yahoo.com", "yahoo.ca")
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SM  = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NM  = UIM.getNavigationManager();
        db = SM.getSession().getDatabase();



        tilDeviceId = findViewById(R.id.til_device_id);
        tilName     = findViewById(R.id.til_name);
        tilEmail    = findViewById(R.id.til_email);
        tilPhone    = findViewById(R.id.til_phone);
        tilRole     = findViewById(R.id.til_role);

        etDeviceId  = findViewById(R.id.et_device_id);
        etName      = findViewById(R.id.et_name);
        etEmail     = findViewById(R.id.et_email);
        etPhone     = findViewById(R.id.et_phone);
        etRole      = findViewById(R.id.et_role);
        btnSignup   = findViewById(R.id.btn_signup);
        btnMore     = findViewById(R.id.btn_more);

        String[] roles = new String[]{Roles.ENTRANT, Roles.ORGANIZER, Roles.ADMIN};
        etRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles));
        etRole.setText(Roles.ENTRANT, false);
        etRole.setOnClickListener(v -> etRole.showDropDown());
        etRole.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) etRole.showDropDown(); });

        try {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(id)) etDeviceId.setText(id);
        } catch (Exception ignored) {}

        btnSignup.setOnClickListener(v -> submit());
        btnMore.setOnClickListener(v -> Toast.makeText(this, "Go to Login screen", Toast.LENGTH_SHORT).show());
    }

    private void submit() {
        clearErrors();

        String deviceId = txt(etDeviceId); // UI is read-only, but we still read it for display
        String name     = txt(etName);
        String email    = txt(etEmail);
        String phone    = txt(etPhone);
        String role     = etRole.getText() == null ? "" : etRole.getText().toString().trim();

        boolean ok = true;
        if (deviceId.isEmpty()) { tilDeviceId.setError("Device ID required"); ok = false; }
        if (name.isEmpty())     { tilName.setError("Name required"); ok = false; }

        if (email.isEmpty()) {
            tilEmail.setError("Email required"); ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email"); ok = false;
        } else if (!hasAllowedDomain(email)) {
            tilEmail.setError("Use a valid email address");
            ok = false;
        }

        if (!phone.isEmpty() && phone.length() < 7) { tilPhone.setError("Invalid phone"); ok = false; }
        if (role.isEmpty())     { tilRole.setError("Choose a role"); ok = false; }
        if (!ok) return;

        btnSignup.setEnabled(false);

        db.actorExistsByEmail(email, exists -> {
            if (exists == null) {
                btnSignup.setEnabled(true);
                Toast.makeText(this, "Network error. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (exists) {
                btnSignup.setEnabled(true);
                tilEmail.setError("User already exists with this email");
                return;
            }

            Actor actor = createActorByRole(role, deviceId, name, email, phone);

            // Save to Firestore with role (expects Database.upsertActorWithRole)
            db.upsertActorWithRole(actor, role, success -> {
                btnSignup.setEnabled(true);
                if (Boolean.TRUE.equals(success)) {
                    UIM.setCurrentActor(actor);

                    getSharedPreferences("entrant_profile", MODE_PRIVATE)
                            .edit()
                            .putString("userId", deviceId)
                            .putString("name", name)
                            .putString("email", email)
                            .putString("phone", phone)
                            .putString("role", role)
                            .apply();

                    getSharedPreferences("app", MODE_PRIVATE)
                            .edit()
                            .putBoolean("signed_up", true)
                            .apply();

                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private Actor createActorByRole(String role, String id, String name, String email, String phone) {
        if (Roles.ORGANIZER.equals(role)) return new Organizer(id, name, email, phone);
        if (Roles.ADMIN.equals(role))     return new Administrator(id, name, email, phone);
        return new Entrant(id, name, email, phone, false);
    }

    private void clearErrors() {
        tilDeviceId.setError(null);
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilRole.setError(null);
    }

    private String txt(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean hasAllowedDomain(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return false;
        String domain = email.substring(at + 1).toLowerCase(Locale.US).trim();
        return ALLOWED_DOMAINS.contains(domain);
    }
}
