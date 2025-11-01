package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteActivity extends AppCompatActivity {

    private Button deleteButton;

    private SessionManager SM;
    private ActorManager AM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
    private Result result;

    // Stores the listener manually, didn't really want to use LiveData
    private final ResultListener resultListener = r ->{
        if (result.isSuccess()) {
            NM.goTo(HomePage);
        }else{
            UIM.showFragment(result.getMessage());
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
        deleteButton = findViewById(R.id.activity_delete);
        SM = ((EventsApp) getApplicationContext()).getSessionManager(); // get session manager
        AM = SM.getActorManager();
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();
        result = UIM.getResult();

        deleteButton.setOnClickListener(v -> {
            AM.deleteActor();
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        if (result != null){
            result.addListener(resultListener);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (result != null) {
            result.removeListener(resultListener);
        }
    }
}