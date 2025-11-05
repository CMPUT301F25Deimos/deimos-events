package com.example.deimos_events.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.ui.events.EventArrayAdapter;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentNotificationsBinding;
import com.example.deimos_events.ui.events.EventTest;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    
    private FragmentNotificationsBinding binding;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        ArrayList<EventTest> notificationsList = new ArrayList<>();
        notificationsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        notificationsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, -1, true));
        notificationsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        notificationsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false,-1, false));
        notificationsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        notificationsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, 0, false));
        
        ListView listView = binding.notificationsList;
        NotificationsArrayAdapter adapter = new NotificationsArrayAdapter(requireContext(), notificationsList);
        listView.setAdapter(adapter);
        
        return root;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}