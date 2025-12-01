package com.example.deimos_events.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentNotificationsBinding;
import com.example.deimos_events.managers.NotificationManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.ArrayList;

/**
 * Shows the screen for the notifications page
 */
public class NotificationsFragment extends Fragment {
    
    private FragmentNotificationsBinding binding;
    private SessionManager SM;

    private NotificationManager NM;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        ListView listView = binding.notificationsList;

        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        SM = app.getSessionManager();
        // gets data
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        NM = SM.getNotificationManager();

        ArrayList<Notifications> notificationsList = new ArrayList<>();
        NotificationsArrayAdapter adapter = new NotificationsArrayAdapter(requireContext(), notificationsList, db, actor);
        listView.setAdapter(adapter);
        
        NM.fetchNotifications(actor, notificationsList, adapter);
        
        return root;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}