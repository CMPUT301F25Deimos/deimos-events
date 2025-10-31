package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_delete);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        Button deleteButton;
//        SessionManager SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
//        ActorManager AM = SM.getActorManager();
//        UserInterfaceManager UIM = SM.getUserInterfaceManager();
//        NavigationManager NM = UIM.getNavigationManager();
//
//
//        // setup an observer for the result
//
//        // hmm
//        deleteButton.setOnClickListener(v ->{
//            if (su)
//            AM.deleteActor(); // delete first
//            NM.goTo(LoadingScreen); // wait for async
//        });
//
//    }
}