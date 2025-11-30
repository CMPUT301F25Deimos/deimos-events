package com.example.deimos_events.ui.auth;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.dataclasses.Administrator;
import com.example.deimos_events.dataclasses.Entrant;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.FoundationActivity;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.MainActivity;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.dataclasses.Organizer;
import com.example.deimos_events.R;
import com.example.deimos_events.Roles;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * The {@code SignupActivity} class provides a user interface for new users to create an account
 * within the Deimos Events application. It collects and validates basic user information such as
 * name, email, phone number, and role (Entrant, Organizer, or Admin).
 * <p>
 * On launch this activity checks {@link android.content.SharedPreferences} to see if the user already exists.
 * If such a user exists we recreate the corresponding {@link Actor}, set it as the current actor via the {@link UserInterfaceManager}
 * and the user is then automatically moved to the {@link MainActivity} bypassing the sign-up form.
 * <p>
 * Once the information is validated, it creates an {@link Actor} (or its subclass such as
 * {@link Entrant}, {@link Organizer}, or {@link Administrator}) and stores it in the
 * Firebase database through the {@link ActorManager}. It also caches the profile locally using
 * so that returning users can skip sign-up.
 * </p>
 * <p>
 * Key responsibilities:
 * <ul>
 *     <li>Validate input fields for correctness and allowed email domains.</li>
 *     <li>Create and insert {@link Actor} instances into the database.</li>
 *     <li>Persist user session and navigate to {@link MainActivity} after successful signup.</li>
 *     <li>Skips the sign-up screen if we have already have a user's profile stored</li>
 * </ul>
 * </p>
 *
 * @see ActorManager
 * @see com.example.deimos_events.Session
 * @see com.example.deimos_events.ui.auth
 */
public class SignupActivity extends FoundationActivity {

    private TextInputLayout tilName, tilEmail, tilPhone, tilDeviceId, tilRole;
    private TextInputEditText etDeviceId, etName, etEmail, etPhone;
    private AutoCompleteTextView etRole;
    private MaterialButton btnSignup, btnMore;


    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    private ActorManager AM;

    /**
     * Allowed email domains for validation.
     */
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(
            Arrays.asList("gmail.com", "outlook.com", "hotmail.com", "yahoo.com", "yahoo.ca")
    );

    /**
     * Called when the sign-up screen is created.
     * <p>
     * Initializes UI elements, session managers, and preloads stored user information if
     * the user has previously signed up. If a stored profile exists, the user is redirected
     * to {@link MainActivity} and the sign-up form is skipped.
     * </p>
     * If a user doesn't exist, this method will setup the sign-up layout, wire text fields, set up the role selection
     * dropdown, and automatically fill the device ID using {@link android.provider.Settings.Secure#ANDROID_ID}.
     *
     * @param savedInstanceState saved instance state bundle, if available.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NM = SM.getNavigationManager();
        AM = SM.getActorManager();

        //The following part Idea is taken from: https://stackoverflow.com/questions/5082846/how-to-implement-stay-logged-in-when-user-login-in-to-the-web-application
        //Authored By: Thang Pham
        //Taken By: Harmanjot Kaur Dhaliwal
        //Taken on: November 6th, 2025
        boolean signedUp = getSharedPreferences("app", MODE_PRIVATE)
                .getBoolean("signed_up", false);
        if (signedUp) {
            String userId = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("userId", null);
            String name = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("name", null);
            String email = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("email", null);
            String phone = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("phone", "");
            String role = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("role", Roles.ENTRANT);

            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
                Actor restored = createActorByRole(userId, name, email, phone == null ? "" : phone, role);
                UIM.setCurrentActor(restored);
                NM.goTo(MainActivity.class, NavigationManager.navFlags.RETURN_TO_TASK);
                return;
            }
        }

        // Initialize form fields
        tilDeviceId = findViewById(R.id.til_device_id);
        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilRole = findViewById(R.id.til_role);

        etDeviceId = findViewById(R.id.et_device_id);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etRole = findViewById(R.id.et_role);
        btnSignup = findViewById(R.id.btn_signup);
        btnMore = findViewById(R.id.btn_more);

        // Role selection dropdown
        String[] roles = new String[]{Roles.ENTRANT, Roles.ORGANIZER, Roles.ADMIN};
        etRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles));
        etRole.setText(Roles.ENTRANT, false);
        etRole.setOnClickListener(v -> etRole.showDropDown());
        etRole.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) etRole.showDropDown();
        });

        // Automatically fill device ID
        try {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(id)) etDeviceId.setText(id);
        } catch (Exception ignored) {
        }

        // Set up button listeners
        btnSignup.setOnClickListener(v -> submit());
        btnMore.setOnClickListener(v -> Toast.makeText(this, "Go to Login screen", Toast.LENGTH_SHORT).show());
    }

    /**
     * Validates form fields and attempts to register a new user.
     * <p>
     * If input validation passes, it creates an {@link Actor} and inserts it into the database
     * using {@link ActorManager#insertActor(Actor, java.util.function.Consumer)}.
     * <p>
     * While we are submitting data, the sign-up button is disabled.
     * Database operations are asynchronous. When completed this method will:
     * <ul>
     *     <li>Displays an error message to the user on failure and then keeps them on the sign-up screen</li>
     *     <li>If submission succeeds, it saves the users data locally in {@link android.content.SharedPreferences}, then
     *     indicates that the user has completed the sign-up, and moves the user to the @{@link MainActivity}</li>
     * </ul>
     */
    private void submit() {
        clearErrors();

        String deviceId = txt(etDeviceId);
        String name = txt(etName);
        String email = txt(etEmail);
        String phone = txt(etPhone);
        String role = etRole.getText() == null ? "" : etRole.getText().toString().trim();

        boolean ok = true;
        if (deviceId.isEmpty()) {
            tilDeviceId.setError("Device ID required");
            ok = false;
        }
        if (name.isEmpty()) {
            tilName.setError("Name required");
            ok = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email required");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            ok = false;
        } else if (!hasAllowedDomain(email)) {
            tilEmail.setError("Use a valid email address");
            ok = false;
        }

        if (!phone.isEmpty() && phone.length() < 7) {
            tilPhone.setError("Invalid phone");
            ok = false;
        }
        if (role.isEmpty()) {
            tilRole.setError("Choose a role");
            ok = false;
        }
        if (!ok) return;

        btnSignup.setEnabled(false);

        Actor actor = createActorByRole(deviceId, name, email, phone, role);

        AM.insertActor(actor, res -> {
            btnSignup.setEnabled(true);
            String UIErrorMessage = "";
            if (!res.isSuccess()) {
                if (res.getMessage().equals("Database Failed to Read")) {
                    UIErrorMessage = "Network Error. Try Again";
                }
                if (res.getMessage().equals("Actor already exists")) {
                    UIErrorMessage = "User already exists with this email";
                }
                Toast.makeText(this, UIErrorMessage, Toast.LENGTH_SHORT).show();
                UIM.clearCurrentActor();
                return;
            } else {
                // update the shared preference so we don't need to sign in again
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
                NM.goTo(MainActivity.class, NavigationManager.navFlags.RETURN_TO_TASK);
            }
        });
    }

    /**
     * Creates a subclass of {@link Actor} based on the given role.
     *
     * @param id    unique device identifier for the user.
     * @param name  user's full name.
     * @param email user's email address.
     * @param phone user's phone number.
     * @param role  role selection (Entrant, Organizer, Admin).
     * @return a new {@link Actor} instance matching the selected role.
     */
    private Actor createActorByRole(String id, String name, String email, String phone, String role) {
        if (Roles.ORGANIZER.equals(role)) return new Organizer(id, name, email, phone);
        if (Roles.ADMIN.equals(role)) return new Administrator(id, name, email, phone);
        return new Entrant(id, name, email, phone, false);
    }

    /**
     * Clears all validation error messages from input fields.
     */
    private void clearErrors() {
        tilDeviceId.setError(null);
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilRole.setError(null);
    }

    /**
     * Helper to safely extract trimmed text from {@link TextInputEditText}.
     *
     * @param et input field.
     * @return the trimmed text, or an empty string if null.
     */
    private String txt(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /**
     * Checks if the email belongs to an approved domain.
     *
     * @param email the user email to verify.
     * @return true if the domain is in {@link #ALLOWED_DOMAINS}, false otherwise.
     */
    private boolean hasAllowedDomain(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return false;
        String domain = email.substring(at + 1).toLowerCase(Locale.US).trim();
        return ALLOWED_DOMAINS.contains(domain);
    }
}
