package com.example.deimos_events;

import android.os.Bundle;

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

public class MainActivity extends FoundationActivity {
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
    private String role;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SM  = ((EventsApp) getApplicationContext()).getSessionManager();
        NM  = SM.getNavigationManager();

        if (!getSharedPreferences("app", MODE_PRIVATE).getBoolean("signed_up", false)) {
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

        EventsApp app = (EventsApp) getApplicationContext();
        SM = app.getSessionManager();

        // gets data
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();

        // gets role of current user to determine what navigation will look like
        db.getActorRole(session.getCurrentActor(), actorRole -> {
            role = actorRole;
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
                        R.id.navigation_profile
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
        });


    }

}
