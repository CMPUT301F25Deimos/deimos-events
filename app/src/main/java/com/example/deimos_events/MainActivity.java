package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.deimos_events.databinding.ActivityMainBinding;

public class MainActivity extends FoundationActivity {

    // grab Android stuff that you need
    private Button navigateButton;

    private EditText nameBox;
    private EditText emailBox;
    private EditText phoneBox;
    private EditText IDBox;

    // grab the our system stuff
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instantiate Android stuff
        navigateButton = findViewById(R.id.navigate_button);



        // instantiate Our system stuff, grab managers that you need
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();


        // If you need user interface information, ask the UIM
        // Call UIM to grab the things from the session
        // Example username = UIM.getUsername();
        // setup interactive elements
        navigateButton.setOnClickListener(v -> {
            NM.goTo(SignInActivity.class);
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_events, R.id.navigation_notifications, R.id.navigation_qr_code, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    }
    
