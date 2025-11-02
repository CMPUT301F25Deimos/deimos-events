package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignInActivity extends FoundationActivity {

    // grab Android stuff that you need
    private Button signInButton;

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
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instantiate Android stuff
        signInButton = findViewById(R.id.sign_in);
        nameBox = findViewById(R.id.input_name);
        emailBox = findViewById(R.id.input_email);
        IDBox = findViewById(R.id.input_ID);
        phoneBox = findViewById(R.id.input_phone);



        // instantiate Our system stuff, grab managers that you need
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();


        // If you need user interface information, ask the UIM
        // Call UIM to grab the things from the session
        // Example username = UIM.getUsername();


        // setup interactive elements
        signInButton.setOnClickListener(v -> {
            String name = nameBox.getText().toString();
            String email = emailBox.getText().toString();
            String phone = phoneBox.getText().toString();
            String ID = IDBox.getText().toString();
            Actor newActor = new Actor(ID, name, email, phone);
            SM.getSession().setCurrentActor(newActor);
            NM.goTo(CreateActivity.class);
        });

    }
}