package com.example.deimos_events.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.Event;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentEventsBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EventsFragment extends Fragment {
    
    private FragmentEventsBinding binding;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);
        
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = binding.getRoot().findViewById(R.id.listView); // change if your ListView has another id

// Suppose you already have a list of events (e.g., from Firestore or dummy data)
        List<Event> eventsList = new ArrayList<>();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                eventsList.stream().map(Event::getTitle).collect(Collectors.toList())
        );

        listView.setAdapter(adapter);

// Open EventDetailActivity when an event is tapped
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventsList.get(position);
            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
            intent.putExtra("eventId", selectedEvent.getId());  // just pass the id
            startActivity(intent);
        });


//        final TextView textView = binding.textEvents;
//        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}