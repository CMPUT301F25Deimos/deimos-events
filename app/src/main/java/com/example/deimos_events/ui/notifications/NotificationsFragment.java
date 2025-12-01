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
 * Displays the notifications page for an entrant.
 *
 * <p>This fragment retrieves the current user's notifications from
 * the backend and displays them in a ListView using
 * {@link NotificationsArrayAdapter}. Notifications include:
 * <ul>
 *     <li>General messages sent by organizers</li>
 *     <li>Waitlist / lottery results</li>
 *     <li>Accepted / declined offer updates</li>
 * </ul>
 *
 * <p>The fragment fetches data through {@link NotificationManager}
 * and listens for updates through the shared {@link SessionManager}.</p>
 */
public class NotificationsFragment extends Fragment {
    
    private FragmentNotificationsBinding binding;
    private SessionManager SM;

    private NotificationManager NM;
    /**
     * Inflates the notifications UI, initializes required managers, sets up the
     * ListView adapter, and fetches notifications for the current user.
     *
     * <p>Process:</p>
     * <ol>
     *     <li>Inflate the fragment layout and bind UI elements</li>
     *     <li>Retrieve {@link SessionManager}, {@link Actor}, and {@link IDatabase}</li>
     *     <li>Attach {@link NotificationsArrayAdapter} to the ListView</li>
     *     <li>Use {@link NotificationManager} to populate the list asynchronously</li>
     * </ol>
     *
     * @param inflater inflater used to inflate the layout
     * @param container optional parent view
     * @param savedInstanceState previously saved state (unused)
     * @return the root view for this fragment
     */
    
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
    /**
     * Cleans up fragment references when the view is destroyed.
     *
     * <p>This prevents memory leaks since ViewBinding holds references
     * to the view hierarchy.</p>
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}