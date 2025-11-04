package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentEventsBinding;
import com.example.deimos_events.ui.notifications.NotificationsFragment;

public class EventsFragment extends Fragment {
    
    private FragmentEventsBinding binding;
    private Button add;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final Button add  =binding.Add;
        add.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);

            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_events,true).build();
            navController.navigate(R.id.navigation_create, null , navOptions);
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