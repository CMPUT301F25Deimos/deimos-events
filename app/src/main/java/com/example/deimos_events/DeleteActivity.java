package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteActivity extends AppCompatActivity {

    // grab Android stuff that you need
    private Button deleteButton;

    // grab the our system stuff
    private SessionManager SM;
    private ActorManager AM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    // Implement listener to decide what to do.
    private final ResultListener resultListener = r ->{
        if (result.isSuccess()) {
            NM.goTo(HomePage);
        }else{
            UIM.showFragment(result.getMessage()); // show notification
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instantiate Android stuff
        deleteButton = findViewById(R.id.activity_delete);



        // instantiate Our system stuff, grab managers that you need
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        AM = SM.getActorManager();
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();


        // If you need user interface information, ask the UIM
            // Call UIM to grab the things from the session
            // Example username = UIM.getUsername();


        // setup interactive elements
        deleteButton.setOnClickListener(v -> {
            AM.deleteActor(); // no longer grabs result, instead asks manager to modify
        });

        // Attach listener
        UIM.attachListener(ResultListener);





    }
}