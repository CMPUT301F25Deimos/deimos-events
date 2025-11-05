package com.example.deimos_events;

import androidx.appcompat.app.AppCompatActivity;

public abstract class FoundationActivity extends AppCompatActivity {
    protected SessionManager SM;

    @Override
    protected void onStart(){
        super.onStart();
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        SM.getSession().setActivity(this);
    }
}

