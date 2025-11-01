package com.example.deimos_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateActivity extends AppCompatActivity {

    // grab Android stuff that you need
    private Button createButton;

    // grab the our system stuff
    private SessionManager SM;
    private ActorManager AM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    // Implement listener to decide what to do.
    private final ResultListener resultListener = result ->{
        if (result.isSuccess()) {
            NM.goTo(DisplayDataActivity.class);
        }else{
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show(); // show notification
            System.out.println("Fail");
            NM.goTo(MainActivity.class);
        }
        createButton.setEnabled(Boolean.TRUE);
        UIM.clearResult();
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instantiate Android stuff
        createButton = findViewById(R.id.create_button);
        // instantiate Our system stuff, grab managers that you need
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        AM = SM.getActorManager();
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();

        // If you need user interface information, ask the UIM
        // Call UIM to grab the things from the session
        // Example username = UIM.getUsername();


        // setup interactive elements
        createButton.setOnClickListener(v -> {
            AM.createActor(); // no longer grabs result, instead asks manager to modify
            createButton.setEnabled(Boolean.FALSE);
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        SM.getSession().setActivity(this);
        UIM.attachResultListener(resultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIM.clearResultListener();
    }

}
