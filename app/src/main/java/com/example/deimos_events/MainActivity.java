package com.example.deimos_events;

import android.os.Bundle;

import com.example.deimos_events.ui.auth.SignupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.deimos_events.databinding.ActivityMainBinding;

public class MainActivity extends FoundationActivity {
    private SessionManager       SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
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

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_events,
                R.id.navigation_notifications,
                R.id.navigation_qr_code,
                R.id.navigation_profile
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
