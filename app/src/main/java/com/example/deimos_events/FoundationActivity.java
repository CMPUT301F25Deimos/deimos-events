package com.example.deimos_events;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.managers.SessionManager;

public abstract class FoundationActivity extends AppCompatActivity {
    protected SessionManager SM;
    protected NavigationManager NM;

    @Override
    protected  void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        NM = SM.getNavigationManager();
        NM.setActivity(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        NM = SM.getNavigationManager();
        NM.setActivity(this);
    }
}

