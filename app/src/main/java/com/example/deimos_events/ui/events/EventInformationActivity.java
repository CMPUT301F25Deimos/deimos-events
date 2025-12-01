package com.example.deimos_events.ui.events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;

/**
 * Activity that displays detailed information about a selected {@link Event}.
 *
 * <p>This screen is shown to Entrants and Organizers when they tap an event
 * in the event list. It shows:</p>
 *
 * <ul>
 *     <li>Event title and description</li>
 *     <li>Guidelines and participation criteria</li>
 *     <li>Date, time, and location information</li>
 *     <li>Poster image</li>
 *     <li>Waiting list count for the event</li>
 * </ul>
 *
 * <p>Entrants may also join the event waitlist using the “Join” button.
 * The activity retrieves the current event from the shared {@link SessionManager}
 * through the {@link UserInterfaceManager}.</p>
 */

public class EventInformationActivity  extends AppCompatActivity {

    private TextView EventTitle;
    private TextView description;
    private TextView Guidelines;
    private TextView Criteria;
    private TextView Location;
    private TextView eventDate;
    private TextView eventTime;

    private TextView waitlisted;
    private Button returnButton;
    private Button signUpButton;
    private ImageView eventPoster;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private EventManager EM;
    /**
     * Initializes the event information screen and populates all UI elements
     * with data from the currently selected event.
     *
     * <p>This method performs the following tasks:</p>
     * <ul>
     *     <li>Retrieves the current {@link Event} from the session</li>
     *     <li>Binds all UI components (text, buttons, image)</li>
     *     <li>Displays event title, description, guidelines, and criteria</li>
     *     <li>Loads the poster image from Base64 if available</li>
     *     <li>Displays event location, date, and time</li>
     *     <li>Queries and displays the waiting list count</li>
     *     <li>Handles user actions such as returning or joining the waitlist</li>
     * </ul>
     *
     * @param savedInstanceState previously saved state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_event_info);

        //Initializing all buttons and textViews
        returnButton = findViewById(R.id.btnReturn);
        signUpButton = findViewById(R.id.btnJoin);

        EventTitle = findViewById(R.id.EventTitle);
        description = findViewById(R.id.DescriptionBody);
        Guidelines = findViewById(R.id.GuidelinesDescription);
        Criteria = findViewById(R.id.Criteria);
        waitlisted = findViewById(R.id.EventWaitlisted);
        Location = findViewById(R.id.EventLocation);
        eventDate = findViewById(R.id.EventDate);
        eventTime = findViewById(R.id.EventTime);
        eventPoster = findViewById(R.id.imgEvent);


        //Grabbing all the needed managers
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        EM = SM.getEventManager();

        Event currentEvent = UIM.getCurrentEvent();
        //Setting all the elements of the UI
        EventTitle.setText(currentEvent.getTitle());
        description.setText(currentEvent.getDescription());

        Guidelines.setText(currentEvent.getGuidelines());
        Criteria.setText(currentEvent.getCriteria());

        String Image  = currentEvent.getPosterId();

        if (currentEvent.getLocation() != null){
            Location.setText("Location: " + currentEvent.getLocation());}
        else{
            Location.setText("Location: ");
        }
        if (currentEvent.getDate() != null) {
            eventDate.setText("Date: " + currentEvent.getDate());
        }else{
            eventDate.setText("Date: ");
        }
        if (currentEvent.getTime() != null) {
            eventTime.setText("Time: " + currentEvent.getTime());
        }else{
            eventTime.setText("Time: ");
        }
        //Getting the length of the waiting list from the database
        EM.getWaitingListCount(currentEvent.getId(), count->{
            runOnUiThread(()->waitlisted.setText("Waiting List Count: " + count));
        });

        if (Image != null && !Image.isEmpty()) {
            byte[] decodedBytes = android.util.Base64.decode(Image, android.util.Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            eventPoster.setImageBitmap(decodedBitmap);
        }

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
