package com.example.deimos_events.ui.picker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.deimos_events.EventsApp;
import com.example.deimos_events.Event;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PickerFragment extends Fragment {

    private ImageView imageView;
    private TextView descriptionView, placeholderDescription, spotsFilled, listInfo, pick;
    private ListView listView;
    private Button btnInvited, btnCancelled, btnEnrolled, btnWaitlist;
    private EditText numberToPick;
    private Button selectButton, backButton;

    private RegistrationAdapter registrationAdapter;
    private List<Registration> registrationList = new ArrayList<>();
    private List<Registration> waitingList = new ArrayList<>();

    private Event event;
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
        pick = view.findViewById(R.id.pick);
        placeholderDescription = view.findViewById(R.id.placeholder_description);
        spotsFilled = view.findViewById(R.id.spots_filled);
        listInfo = view.findViewById(R.id.list_info);
        numberToPick = view.findViewById(R.id.number);
        selectButton = view.findViewById(R.id.select);
        backButton = view.findViewById(R.id.back);

        listView = view.findViewById(R.id.listView);
        btnInvited = view.findViewById(R.id.btnInvited);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        btnEnrolled = view.findViewById(R.id.btnEnrolled);
        btnWaitlist = view.findViewById(R.id.btnWaitlist);

        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        EM = SM.getEventManager();
        event = SM.getSession().getCurrentEvent();

        byte[] decodedBytes = Base64.decode(event.getPosterId(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        imageView.setImageBitmap(bmp);

        placeholderDescription.setText(event.getDescription());
        registrationAdapter = new RegistrationAdapter(getContext(), registrationList, this::loadList);
        listView.setAdapter(registrationAdapter);
        btnInvited.setOnClickListener(v -> loadList("Invited"));
        btnCancelled.setOnClickListener(v -> loadList("Cancelled"));
        btnEnrolled.setOnClickListener(v -> loadList("Accepted"));
        btnWaitlist.setOnClickListener(v -> loadList("Pending"));

        loadList("Pending");

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

            if (!event.getParticipantCap().equals(-1) && count > waitingList.size()) {
                Toast.makeText(getContext(), "Not enough people in waiting list to sample", Toast.LENGTH_SHORT).show();
                return;
            }

            Collections.shuffle(waitingList);
            List<Registration> selected = waitingList.subList(0, count);

            for (Registration registration : selected) {
                EM.inviteEntrant(registration.getId(), result -> {
                    if (result) {
                        Toast.makeText(getContext(), "Invited user with id: " + registration.getEntrantId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.popBackStack();
        });

        return view;
    }

    private void loadList(String status) {
        EM.getRegistrationsByStatus(event.getId(), status, registrations -> {
            registrationList.clear();
            registrationList.addAll(registrations);
            registrationAdapter.notifyDataSetChanged();

            waitingList.clear();
            boolean isWaitlist = "Pending".equals(status);
            waitingList.addAll(registrations);

            int visibility = isWaitlist ? View.VISIBLE : View.GONE;
            pick.setVisibility(visibility);
            numberToPick.setVisibility(visibility);
            selectButton.setVisibility(visibility);

            updateListInfo(status);
        });
        EM.getRegistrationsByStatus(event.getId(), "Accepted", registrations -> {
            spotsFilled.setText("Spots Filled: " + registrations.size() + "/" + event.getParticipantCap());
        });
    }

    private void updateListInfo(String status) {
        if (Objects.equals(status, "Pending")) {
            listInfo.setText("Waitlist: " + waitingList.size());
        }
        if (Objects.equals(status, "Cancelled")) {
            listInfo.setText("Cancelled List: " + waitingList.size());
        }
        if (Objects.equals(status, "Accepted")) {
            listInfo.setText("Accepted List: " + waitingList.size());
        }
        if (Objects.equals(status, "Invited")) {
            listInfo.setText("Invited List: " + waitingList.size());
        }
    }
}
