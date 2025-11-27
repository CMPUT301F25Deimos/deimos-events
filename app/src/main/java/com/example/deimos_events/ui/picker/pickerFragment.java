package com.example.deimos_events.ui.picker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.Entrant;
import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class pickerFragment extends Fragment {

    private ImageView imageView;
    private TextView descriptionView, placeholderDescription, spotsFilled, waitingListNo;
    private ListView listView;
    private EditText numberToPick;
    private Button selectButton, backButton;

    private Event event;
    private List<String> attendees;
    private List<Entrant> waitingList;
    private EventManager EM;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_participant_picker, container, false);

        imageView = view.findViewById(R.id.imageView);
        descriptionView = view.findViewById(R.id.description);
        placeholderDescription = view.findViewById(R.id.placeholder_description);
        spotsFilled = view.findViewById(R.id.spots_filled);
        waitingListNo = view.findViewById(R.id.waiting_list_no);
        listView = view.findViewById(R.id.listView);
        numberToPick = view.findViewById(R.id.number);
        selectButton = view.findViewById(R.id.select);
        backButton = view.findViewById(R.id.back);

        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        event = SM.getSession().getCurrentEvent();
        ActorManager AM  = SM.getActorManager();
        this.EM = SM.getEventManager();

        byte[] decodedBytes = Base64.decode(event.getPosterId(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        attendees = new ArrayList<>();
        waitingList = new ArrayList<>();

        imageView.setImageBitmap(bmp);
        placeholderDescription.setText(event.getDescription());

        this.EM.getEventAttendees(event.getId(), eventAttendees -> {
            attendees.clear();
            for (Entrant e : eventAttendees) {
                attendees.add(e.getName());
            }

            spotsFilled.setText("Spots Filled: "
                    + attendees.size() + "/" + event.getParticipantCap());
        });

        this.EM.getWaitlistEntrants(event.getId(), waitingListEntrants -> {
            waitingListNo.setText("Waiting List: " + waitingListEntrants.size());
            waitingList.addAll(waitingListEntrants);

            ArrayAdapter<Entrant> adapter =
                    new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, waitingList);

            listView.setAdapter(adapter);
        });

        selectButton.setOnClickListener(v -> {

            String numText = numberToPick.getText().toString().trim();
            if (numText.isEmpty()) {
                Toast.makeText(getContext(), "Enter a number", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = Integer.parseInt(numText);

            if (count <= 0) {
                Toast.makeText(getContext(), "Number must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (count > waitingList.size()) {
                Toast.makeText(getContext(), "Not enough people in waiting list", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Entrant> selectedEntrants = waitingList.subList(0, count);

            Toast.makeText(getContext(),
                    "Selected: " + selectedEntrants.size() + " people",
                    Toast.LENGTH_SHORT).show();

            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.navigation_organizers_events);
        });

        backButton.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.popBackStack();
        });

        return view;
    }
}
