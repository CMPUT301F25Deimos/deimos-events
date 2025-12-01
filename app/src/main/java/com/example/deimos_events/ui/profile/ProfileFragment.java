package com.example.deimos_events.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.Actor;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.example.deimos_events.databinding.FragmentProfileBinding;
import com.example.deimos_events.ui.auth.SignupActivity;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Fragment responsible for rendering and editing the current user's profile.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Display persisted profile attributes (name, email, phone, role).</li>
 *   <li>Allow inline edits with basic validation (email format and domain allowlist).</li>
 *   <li>Toggle notification preference in {@link SharedPreferences}.</li>
 *   <li>Handle account deletion workflow via {@link ActorManager} and navigation reset.</li>
 * </ul>
 * This fragment delegates data mutations to managers obtained from {@link SessionManager}.
 */
public class ProfileFragment extends Fragment {

    /** ViewBinding for the Profile layout. */
    private FragmentProfileBinding binding;
    /** ViewModel that exposes UI state (title and current {@link Actor}). */
    private ProfileViewModel profileViewModel;

    /** SharedPreferences file name for app-level notification preferences. */
    private static final String SP = "entrant_prefs";
    /** SharedPreferences key for the notification opt-in flag. */
    private static final String KEY_NOTIFY = "receive_notifications";

    /** Email domain allowlist used for basic validation. */
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(
            Arrays.asList("gmail.com", "outlook.com", "hotmail.com", "yahoo.com", "yahoo.ca")
    );

    // Managers and session references (injected via EventsApp/SessionManager)
    private ActorManager AM;
    private SessionManager SM;
    private Session session;
    private UserInterfaceManager UIM;
    private NavigationManager NaM;
    private IDatabase db;

    /**
     * Inflates the view, wires up managers/view model, restores persisted profile values,
     * and sets click handlers for update/delete and the notification toggle.
     *
     * @param inflater  layout inflater
     * @param container parent view group
     * @param savedInstanceState saved state bundle, if any
     * @return the root view for this fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SM = ((EventsApp) requireActivity().getApplication()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NaM = SM.getNavigationManager();
        AM = SM.getActorManager();
        session = SM.getSession();
        db = session.getDatabase();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        profileViewModel.getActor().observe(getViewLifecycleOwner(), this::bindActorCard);

        // Restore locally persisted profile snapshot (for fast UI before remote load).
        SharedPreferences prof = requireContext().getSharedPreferences("entrant_profile", Context.MODE_PRIVATE);
        String savedName  = prof.getString("name", null);
        String savedEmail = prof.getString("email", null);
        String savedPhone = prof.getString("phone", null);
        String savedId    = prof.getString("userId", "tempUserId");
        String savedRole  = prof.getString("role", "Entrant");
        Boolean savedNotificationsPreference  = prof.getBoolean("notificationsPreference", true);

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedName)) {
            profileViewModel.setActor(new Actor(savedId, savedName, savedEmail, savedPhone == null ? "" : savedPhone, savedRole, savedNotificationsPreference));
        }
        if (binding.roleText != null) binding.roleText.setText("Role: " + savedRole);

        // Wire notification toggle to shared preferences.
        SharedPreferences sp = requireContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        boolean initial = sp.getBoolean(KEY_NOTIFY, true);
        if (binding.notifySwitch != null) {
            binding.notifySwitch.setChecked(initial);
            binding.notifySwitch.setOnCheckedChangeListener((btn, checked) ->
                    sp.edit().putBoolean(KEY_NOTIFY, checked).apply());
        }

        // Profile edit and delete actions.
        if (binding.updateButton != null) {
            binding.updateButton.setOnClickListener(v -> showInlineEditDialog());
        }
        if (binding.deleteButton != null) {
            binding.deleteButton.setOnClickListener(v -> showDeleteDialog());
        }

        if (binding.joinedText != null) {
            binding.joinedText.setText("Joined: " + DateFormat.getDateInstance().format(new Date()));
        }
        
        // shows whether the switch is turned on or turned off (AKA its state)
        db.getNotificationsPreference(session.getCurrentActor(), notificationPref -> {
            if (notificationPref != null)
                binding.notifySwitch.setChecked(notificationPref);
            else
                binding.notifySwitch.setChecked(false);
        });
        
        // changes preferences
        binding.notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                db.setNotificationsPreference(session.getCurrentActor(), isChecked);
            }
        });
        
        
        return root;
    }

    /**
     * Binds the provided {@link Actor} data into the profile header card.
     *
     * @param a the actor to display; ignored if {@code null}
     */
    private void bindActorCard(Actor a) {
        if (a == null) return;
        if (binding.nameText != null)  binding.nameText.setText(a.getName());
        if (binding.emailText != null) binding.emailText.setText("Email: " + a.getEmail());
        if (binding.phoneText != null) {
            binding.phoneText.setText("Phone Number: " +
                    (TextUtils.isEmpty(a.getPhoneNumber()) ? "—" : a.getPhoneNumber()));
        }
    }

    /**
     * Sets up the overflow menu for switching users and handles its selection.
     *
     * @param view   the root view
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.core.view.MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new androidx.core.view.MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu);
            }
            //The following part Idea is taken from: https://developer.android.com/training/data-storage/shared-preferences
            //Authored By: Android Developers
            //Taken By: Harmanjot Kaur Dhaliwal
            //Taken on: November 2nd, 2025
            @Override
            public boolean onMenuItemSelected(@NonNull android.view.MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_switch_user) {
                    // Clear local session flags and navigate to sign-up screen.
                    requireContext().getSharedPreferences("app", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("signed_up", false)
                            .apply();
                    requireContext().getSharedPreferences("entrant_profile", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();

                    startActivity(
                            new android.content.Intent(requireContext(), com.example.deimos_events.ui.auth.SignupActivity.class)
                                    .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK | android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                    requireActivity().finish();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), androidx.lifecycle.Lifecycle.State.RESUMED);
    }

    /**
     * Shows an inline edit dialog for name, email, and phone with basic validation.
     * On save, updates via {@link ActorManager#updateActor(Actor, Actor, com.example.deimos_events.ResultCallback)}
     * and persists the snapshot into {@link SharedPreferences}.
     */
    private void showInlineEditDialog() {
        Actor cur = profileViewModel.getActor().getValue();

        final SharedPreferences prof = requireContext().getSharedPreferences("entrant_profile", Context.MODE_PRIVATE);
        final String currentRole = prof.getString("role", "Entrant");
        final String currentEmail = prof.getString("email", "");

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        EditText etName  = new EditText(requireContext()); etName.setHint("Full name");
        EditText etEmail = new EditText(requireContext()); etEmail.setHint("Email");
        EditText etPhone = new EditText(requireContext()); etPhone.setHint("Phone (optional)");

        if (cur != null) {
            etName.setText(cur.getName());
            etEmail.setText(cur.getEmail());
            etPhone.setText(cur.getPhoneNumber());
        }

        container.addView(etName,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = pad / 2;
        container.addView(etEmail, lp);
        container.addView(etPhone, lp);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", (d, w) -> {
                    String name  = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast t = Toast.makeText(requireContext(), "Name and Email required", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !hasAllowedDomain(email)) {
                        Toast.makeText(requireContext(), "Use a valid email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!email.equalsIgnoreCase(currentEmail)) {
                        AM.actorExistsByEmail(email, result -> {
                            if (!result.isNull()) {
                                if (result.isSuccess()) {
                                    Toast.makeText(requireContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    performProfileSave(name, email, phone, currentRole, true);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Network error. Try again.", Toast.LENGTH_SHORT).show();
                            }});
                    } else {
                        performProfileSave(name, email, phone, currentRole, true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Persists the edited profile through {@link ActorManager}, updates the ViewModel, and
     * writes a snapshot to {@link SharedPreferences} for quick subsequent loads.
     *
     * @param name  new display name
     * @param email new email address
     * @param phone new phone number (may be empty)
     * @param role  unchanged role to persist alongside the snapshot
     */
    private void performProfileSave(String name, String email, String phone, String role, Boolean notificationsPreference) {
        Actor cur = profileViewModel.getActor().getValue();
        if (cur == null) {
            Toast.makeText(requireContext(), "No profile loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Actor actor = new Actor(cur.getDeviceIdentifier(), name, email, phone, cur.getRole(), notificationsPreference);
        AM.updateActor(cur, actor, res->{
            if (res.isSuccess()){
                requireContext().getSharedPreferences("entrant_profile", Context.MODE_PRIVATE)
                        .edit()
                        .putString("userId", cur.getDeviceIdentifier())
                        .putString("name", name)
                        .putString("email", email)
                        .putString("phone", phone)
                        .putString("role", role)
                        .apply();
                profileViewModel.updateActor(name, email, phone, notificationsPreference);
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Checks whether the given email belongs to an allowed domain.
     *
     * @param email email address to validate
     * @return {@code true} if the domain is in {@link #ALLOWED_DOMAINS}, otherwise {@code false}
     */

    //The following part Idea is taken from: https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression/51332395#51332395
    //Authored By: Stack Overflow
    //Taken By: Harmanjot Kaur Dhaliwal
    //Taken on: November 2nd, 2025
    private boolean hasAllowedDomain(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return false;
        String domain = email.substring(at + 1).toLowerCase(Locale.US).trim();
        return ALLOWED_DOMAINS.contains(domain);
    }

    /**
     * Shows a confirmation dialog for account deletion and, if confirmed, delegates the cascade
     * deletion to {@link ActorManager#deleteEntrantCascade(Actor, com.example.deimos_events.ResultCallback)}.
     * On success, clears local session state and navigates back to {@link SignupActivity}.
     */
    private void showDeleteDialog() {
        View content = LayoutInflater.from(requireContext())
                .inflate(R.layout.remove_profile, null, false);

        View confirmBtn = content.findViewById(R.id.confirm_delete_btn);
        View returnBtn  = content.findViewById(R.id.return_btn);

        if (confirmBtn == null || returnBtn == null) {
            throw new IllegalStateException("remove_profile.xml missing confirm/return button IDs.");
        }

        AlertDialog dialog =
                new AlertDialog.Builder(requireContext())
                        .setView(content)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        returnBtn.setOnClickListener(v -> dialog.dismiss());

        confirmBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Deleting account…", Toast.LENGTH_SHORT).show();

            String deviceIdentifier = requireContext()
                    .getSharedPreferences("entrant_profile", Context.MODE_PRIVATE)
                    .getString("userId", null);

            if (TextUtils.isEmpty(deviceIdentifier)) {
                Toast.makeText(requireContext(), "No user session found.", Toast.LENGTH_SHORT).show();
                return;
            }
            Actor actor = UIM.getCurrentActor();
            AM.deleteEntrantCascade(actor, result -> {
                if (!result.isSuccess()){
                    Toast.makeText(requireContext(), "Failed to delete from Firebase. Try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                requireContext().getSharedPreferences("entrant_profile", Context.MODE_PRIVATE).edit().clear().apply();
                requireContext().getSharedPreferences("app", Context.MODE_PRIVATE).edit()
                        .putBoolean("signed_up", false)
                        .apply();
                try {
                    com.example.deimos_events.EventsApp app =
                            (com.example.deimos_events.EventsApp) requireContext().getApplicationContext();
                    SessionManager sm = app.getSessionManager();
                    com.example.deimos_events.managers.UserInterfaceManager uim = sm.getUserInterfaceManager();
                    uim.setCurrentActor(null);
                } catch (Exception ignored) {}

                if (getActivity() != null) {
                    dialog.dismiss();
                    NaM.goTo(com.example.deimos_events.ui.auth.SignupActivity.class, NavigationManager.navFlags.RESET_TO_NEW_ROOT);
                }
                Toast.makeText(requireContext(), "Your account was deleted.", Toast.LENGTH_SHORT).show();
            });
        });

        dialog.show();
    }

    /** Clears the binding reference to avoid leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
