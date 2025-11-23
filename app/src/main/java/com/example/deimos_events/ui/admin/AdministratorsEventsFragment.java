package com.example.deimos_events.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

public class AdministratorsEventsFragment extends Fragment {

    private SessionManager SM;
    private EventManager EM;
    private RecyclerView recyclerView;
    private AdministratorsEventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administrators_events, container, false);

        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        EM = SM.getEventManager();   // 🔧 changed line

        recyclerView = view.findViewById(R.id.recycler_admin_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AdministratorsEventsAdapter(this::onDeleteEventClicked);
        recyclerView.setAdapter(adapter);

        loadEvents();

        return view;
    }

    private void loadEvents() {
        EM.getAllEvents(events -> {
            requireActivity().runOnUiThread(() -> {
                adapter.setEvents(events);
            });
        });
    }

    private void onDeleteEventClicked(Event event) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove event")
                .setMessage("Are you sure you want to remove this event?\n\n" +
                        (event.getTitle() != null ? event.getTitle() : "Untitled event"))
                .setPositiveButton("Remove", (dialog, which) -> {
                    EM.adminDeleteEvent(event, result -> {
                        requireActivity().runOnUiThread(() -> {
                            if (result.isSuccess()) {
                                Toast.makeText(requireContext(),
                                        "Event removed", Toast.LENGTH_SHORT).show();
                                loadEvents(); // refresh list
                            } else {
                                Toast.makeText(requireContext(),
                                        "Failed to remove event", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
