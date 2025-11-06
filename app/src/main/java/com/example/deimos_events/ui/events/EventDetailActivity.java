/*
package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.Result;
import com.example.deimos_events.R;
import com.example.deimos_events.SessionManager;

public class EventDetailActivity extends AppCompatActivity {

    private Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        TextView eventTitle = findViewById(R.id.event_title);
        Button joinButton = findViewById(R.id.join_waiting_list_button);

        SessionManager sessionManager = ((EventsApp) getApplicationContext()).getSessionManager();
        EventManager eventManager = sessionManager.getEventManager();

        // Get eventId from intent
        String eventId = getIntent().getStringExtra("eventId");
        Event selectedEvent = sessionManager.getEventManager().getEventById(eventId);

        if (selectedEvent != null) {
            eventTitle.setText(selectedEvent.getTitle());
        }

        joinButton.setOnClickListener(v -> {
            if (selectedEvent != null) {
                eventManager.joinWaitingList(selectedEvent);
                Result result = sessionManager.getSession().getResult();
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: Event not found.", Toast.LENGTH_SHORT).show();
            }
        });
        Button leaveButton = findViewById(R.id.leave_waiting_list_button);

        leaveButton.setOnClickListener(v -> {
            if (selectedEvent != null) {
                eventManager.leaveWaitingList(selectedEvent);
                Result result = sessionManager.getSession().getResult();
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();

                // Optional: Update UI immediately (disable join button, etc.)
                if (result.isSuccess()) {
                    joinButton.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "Error: No event selected.", Toast.LENGTH_SHORT).show();
            }
        });

    }}
*/
