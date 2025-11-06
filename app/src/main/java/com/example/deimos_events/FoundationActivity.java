package com.example.deimos_events;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class FoundationActivity extends AppCompatActivity {
    protected SessionManager SM;
    protected UserInterfaceManager UIM;

    @Override
    protected  void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        UIM.setActivity(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        SM.getSession().setActivity(this);
    }
}

