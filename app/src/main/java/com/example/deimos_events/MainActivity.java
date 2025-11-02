package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    // Implement listener to decide what to do.
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

    }

}