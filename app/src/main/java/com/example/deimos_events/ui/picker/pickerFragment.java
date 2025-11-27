package com.example.deimos_events.ui.picker;

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

import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
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
    private List<String> selectedPeople;

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

        SessionManager sm = ((EventsApp) requireActivity().getApplication()).getSessionManager();
        event = sm.getSession().getCurrentEvent();

        if (event == null) {
            Toast.makeText(getContext(), "No event found", Toast.LENGTH_SHORT).show();
            return view;
        }

        byte[] decodedBytes = Base64.decode(event.getPosterId(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        imageView.setImageBitmap(bmp);
        placeholderDescription.setText(event.getDescription());

        spotsFilled.setText("Spots Filled: " + event.getAttendees().size());
        waitingListNo.setText("Waiting List: " + event.getWaitingList().size());

        attendees = new ArrayList<>();
        for (String name : event.getAttendees()) {
            attendees.add(name);
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendees);

        listView.setAdapter(adapter);

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

            if (count > attendees.size()) {
                Toast.makeText(getContext(), "Not enough attendees", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedPeople = attendees.subList(0, count);

            Toast.makeText(getContext(),
                    "Selected: " + selectedPeople.size() + " people",
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
