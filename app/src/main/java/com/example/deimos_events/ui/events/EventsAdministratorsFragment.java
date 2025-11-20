package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.databinding.FragmentAdministratorsEventsBinding;

public class EventsAdministratorsFragment extends Fragment {
    private FragmentAdministratorsEventsBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAdministratorsEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        return root;
    }
}
