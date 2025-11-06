package com.example.deimos_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.function.Consumer;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_event_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        NM = UIM.getNavigationManager();
        EM = SM.getEventManager();

        //getSelectedEvent() to be implemented
        selectedEvent = UIM.getSelectedEvent();

        EventTitle.setText(selectedEvent.getTitle());
        description.setText(selectedEvent.getDescription());

        Guidelines.setText(selectedEvent.getGuidelines());
        Criteria.setText(selectedEvent.getCriteria());


        Location.setText("Location: " + selectedEvent.getLocation());
        //returns Date object, so fix that later
        eventDate.setText("Date: " + selectedEvent.getDate());
        eventTime.setText("Time: " + selectedEvent.getTime());
        //participants cap needs to be subtracted but the amount of entrants accepted
        availablespots.setText(String.valueOf(selectedEvent.getParticipantCap()));//needs to be changed so that is updated

        //Implemented this, good for now but check in later
        EM.getWaitingListCount(selectedEvent.getId(), count->{
            runOnUiThread(()->waitlisted.setText("Waiting List Count: " + count));
        });


        //After going back also clear selected event
        returnButton.setOnClickListener(v->NM.goBack());


        signUpButton.setOnClickListener(v->{
            //Implement this
        });









    }
}
