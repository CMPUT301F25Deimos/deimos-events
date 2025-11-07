package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;

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

    private Event selectedEvent;


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

        selectedEvent = UIM.getSelectedEvent();

        EventTitle.setText(selectedEvent.getTitle());
        description.setText(selectedEvent.getDescription());

        Guidelines.setText(selectedEvent.getGuidelines());
        Criteria.setText(selectedEvent.getCriteria());


        Location.setText("Location: " + selectedEvent.getLocation());
        //returns Date object, so fix that later
        eventDate.setText("Date: " + selectedEvent.getDate());
        eventTime.setText("Time: " + selectedEvent.getTime());

        availablespots.setText(String.valueOf(selectedEvent.getParticipantCap()));//needs to be changed so that is updated

        //Implemented this, good for now but check in later
        EM.getWaitingListCount(selectedEvent.getId(), count->{
            runOnUiThread(()->waitlisted.setText("Waiting List Count: " + count));
        });


        //Need to figure out how to go from activity to fragment
        returnButton.setOnClickListener(v-> finish());//fix this


        signUpButton.setOnClickListener(v->{
            EM.addUserToWaitList(selectedEvent.getId(), success->{
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
