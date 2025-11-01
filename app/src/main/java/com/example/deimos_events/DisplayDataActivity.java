package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisplayDataActivity extends AppCompatActivity {

    // grab Android stuff that you need
    private Button navigateButton;

    private TextView titleText;

    // grab the our system stuff
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    // Implement listener to decide what to do.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instantiate Android stuff
        navigateButton = findViewById(R.id.navigate_button);
        titleText = findViewById(R.id.title_text);



        // instantiate Our system stuff, grab managers that you need
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();


        // If you need user interface information, ask the UIM
        // Call UIM to grab the things from the session
         Actor currentActor = UIM.getActorFromSessionManager();
         String title = "Hello User: " + currentActor.getName();
         titleText.setText(title);





        // setup interactive elements
        navigateButton.setOnClickListener(v -> {
            NM.goTo(DeleteActivity.class);
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        SM.getSession().setActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}