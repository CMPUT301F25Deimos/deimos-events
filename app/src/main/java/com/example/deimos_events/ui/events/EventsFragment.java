package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentEventsBinding;

import java.util.ArrayList;
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

       /* final Button add  = binding.Add;
        add.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);

            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_events,true).build();
            navController.navigate(R.id.navigation_create, null , navOptions);
        });*/
//        final TextView textView = binding.textEvents;
//        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        // data to use in the list
        ArrayList<EventTest> eventsList = new ArrayList<>();
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, -1, true));
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false,-1, false));
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, 0, false));

        ListView listView = binding.eventsList;
        EventArrayAdapter adapter = new EventArrayAdapter(requireContext(), eventsList);
        listView.setAdapter(adapter);
        return root;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}