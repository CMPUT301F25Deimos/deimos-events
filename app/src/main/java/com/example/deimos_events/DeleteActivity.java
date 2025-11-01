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

public class DeleteActivity extends AppCompatActivity {

    // grab Android stuff that you need
    private Button deleteButton;

    // grab the our system stuff
    private SessionManager SM;
    private ActorManager AM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;

    // Implement listener to decide what to do.
    private final ResultListener resultListener = result ->{
        if (result.isSuccess()) {
            NM.goTo(MainActivity.class);
        }else{
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show(); // show notification
            NM.goTo(CreateActivity.class);
        }
        deleteButton.setEnabled(Boolean.TRUE);
        UIM.clearResult();
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
        deleteButton = findViewById(R.id.delete_button);
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
            deleteButton.setEnabled(Boolean.FALSE);
            System.out.println("Hello");
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