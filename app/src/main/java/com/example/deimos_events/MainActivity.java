package com.example.deimos_events;

import android.os.Bundle;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.example.deimos_events.ui.auth.SignupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.deimos_events.databinding.ActivityMainBinding;

/**
 * The main activity that is shown once a user has successfully signed up or their their profile has
 * been successfully retrived and restored. This activity setsup the applications's navigation structure.
 * <p>
 * Once this activity begins it:
 * <ul>
 *     <li>Obtains the shared managers from {@link SessionManager}</li>
 *     <li>Checks if a user already exists in {@link android.content.SharedPreferences}</li>
 *     <li>Checks that a valid {@link Actor} is stored in the session and its value matches the value
 *     stored in {@code "entrant_profile}</li>
 *     <li>If validation fails, it returns the user back to the Signup Activity</li>
 *     <li>If validation succeeds it uses the user's role to configure the navigation graph and
 *     bottom navigation menu accordingly. </li>
 * </ul>
 */

public class MainActivity extends FoundationActivity {
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    private ActorManager AM;
    private String role;
    private ActivityMainBinding binding;
    /**
     * Sets up the main activity and setups up navigation based on the current actor's role.
     * The current actor is stored in the {@link Session} instance, and accessed via the {@link UserInterfaceManager}
     * This method does the following:
     * <ul>
     *     <li>Retrieves managers from {@link EventsApp}</li>
     *     <li>Verifies that a stored user exists and that the session's current {@link Actor} ID matches the stored user ID</li>
     *     <li>Navigates to Signup Activity if the session or stored profile are invalid or they do not match</li>
     *     <li>Inflates the main layout using the view binding</li>
     *     <li>Initializes the {@link androidx.navigation.NavController} and
     *     {@link com.google.android.material.bottomnavigation.BottomNavigationView}</li>
     *     <li>loading and setting up the navigation graph and bottom navigation menu based on the current actor's role</li>
     *     <li>Connecting the action bar and bottom navigation view to the navigation controller using {@link NavigationUI}</li>
     * </ul>
     * @param savedInstanceState previously saved instance state, if it exists.
     * @see Session
     * @see UserInterfaceManager
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SM  = ((EventsApp) getApplicationContext()).getSessionManager();
        NM  = SM.getNavigationManager();
        AM  = SM.getActorManager();
        UIM = SM.getUserInterfaceManager();
        Actor currentActor = UIM.getCurrentActor();

        if (!getSharedPreferences("app", MODE_PRIVATE).getBoolean("signed_up", false)) {
            NM.goTo(SignupActivity.class, NavigationManager.navFlags.RESET_TO_NEW_ROOT);
            return;
        }
        String storedID = getSharedPreferences("entrant_profile", MODE_PRIVATE).getString("userId", null);

        if (currentActor == null || storedID == null || !storedID.equals(currentActor.getDeviceIdentifier())){
            NM.goTo(SignupActivity.class, NavigationManager.navFlags.RESET_TO_NEW_ROOT);
            return;
        }

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView navView = binding.navView;

        // gets role of current user to determine what navigation will look like
        String role = UIM.getCurrentActor().getRole();


        // Use role to build the nav graph and menu
        navView.getMenu().clear();
        AppBarConfiguration appBarConfiguration = null;
        if (role.equals("Admin")) {
            // sets admins navigation layout
            navController.setGraph(R.navigation.administrators_mobile_navigation);
            navView.inflateMenu(R.menu.bottom_nav_administrators_menu);

                appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_administrators_events,
                        R.id.navigation_images,
                        R.id.navigation_users,
                        R.id.navigation_profile,
                        R.id.navigation_notifications
                ).build();

        } else if (role.equals("Organizer")) {
            // sets navigation layout for organizers
            navController.setGraph(R.navigation.organizers_mobile_navigation);
            navView.inflateMenu(R.menu.bottom_nav_organizers_menu);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_organizers_events,
                    R.id.navigation_notifications,
                    R.id.navigation_qr_code,
                    R.id.navigation_profile
            ).build();
        } else {
            // if user is neither an admin nor organizer, then can only be an entrant
            navController.setGraph(R.navigation.entrants_mobile_navigation);
            navView.inflateMenu(R.menu.bottom_nav_entrants_menu);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_entrants_events,
                    R.id.navigation_notifications,
                    R.id.navigation_qr_code,
                    R.id.navigation_profile
            ).build();

        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Handles navigation when the user presses the Action Bar's "Up" button.
     * <p>
     * Navigates back within the app's navigation graph.
     * <p>
     * This method will try to let the app's NavController control the navigation.
     * If the navigation controller can't do so, it will resort to using the default behaviour
     * given by the Activity class
     * @return true if the NavControlled handled the action; otherwise the returns the result of the default Activity Behaviour
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null) return super.onSupportNavigateUp();
        NavController navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}
