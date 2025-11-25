package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.deimos_events.Event;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;

/**
 * Shows the event information for an event (Entrant Side)
 */

public class EventInformationActivity  extends AppCompatActivity {
    
    private TextView EventTitle;
    private TextView description;
    private TextView Guidelines;
    private TextView Criteria;
    private TextView Location;
    private TextView eventDate;
    private TextView eventTime;
    private TextView availablespots;
    private TextView waitlisted;
    private Button returnButton;
    private Button signUpButton;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
    private EventManager EM;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_event_info);
        
        returnButton = findViewById(R.id.btnReturn);
        signUpButton = findViewById(R.id.btnJoin);
        
        EventTitle = findViewById(R.id.EventTitle);
        description = findViewById(R.id.DescriptionBody);
        Guidelines = findViewById(R.id.GuidelinesDescription);
        Criteria = findViewById(R.id.Criteria);
        availablespots = findViewById(R.id.EventAvailableSpots);
        waitlisted = findViewById(R.id.EventWaitlisted);
        Location = findViewById(R.id.EventLocation);
        eventDate = findViewById(R.id.EventDate);
        eventTime = findViewById(R.id.EventTime);
        
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NM = SM.getNavigationManager();
        EM = SM.getEventManager();
        
        Event currentEvent = UIM.getCurrentEvent();
        //Setting all the elements of the UI
        EventTitle.setText(currentEvent.getTitle());
        description.setText(currentEvent.getDescription());
        
        Guidelines.setText(currentEvent.getGuidelines());
        Criteria.setText(currentEvent.getCriteria());
        
        
        Location.setText("Location: " + currentEvent.getLocation());
        eventDate.setText("Date: " + currentEvent.getDate());
        eventTime.setText("Time: " + currentEvent.getTime());
        
        availablespots.setText(String.valueOf(currentEvent.getParticipantCap()));//needs to be changed so that is updated
        
        //Getting the length of the waiting list from the database
        EM.getWaitingListCount(currentEvent.getId(), count->{
            runOnUiThread(()->waitlisted.setText("Waiting List Count: " + count));
        });
        
        
        returnButton.setOnClickListener(v-> finish());
        
        //Sign up entrant if they are not on the waiting list
        //If they are, then error message appears
        signUpButton.setOnClickListener(v->{
            EM.addUserToWaitList(currentEvent.getId(), success->{
                runOnUiThread(()->{
                    if(success){
                        Toast.makeText(this, "Added to waitlist!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"Already on waitlist or failed.", Toast.LENGTH_SHORT).show();
                    }
                });
                
            });
        });
    }
}
