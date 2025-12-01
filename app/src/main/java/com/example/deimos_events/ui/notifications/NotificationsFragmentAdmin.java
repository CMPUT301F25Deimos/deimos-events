package com.example.deimos_events.ui.notifications;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentAdministratorsNotificationBinding;
import com.example.deimos_events.databinding.FragmentNotificationsBinding;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.managers.NotificationManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.ArrayList;
/**
 * Fragment used by administrators to view notifications sent within the system.
 *
 * <p>This screen retrieves all admin-level notifications from the database and
 * displays them inside a ListView using {@link NotificationsAdminArrayAdapter}.
 * Administrators do not interact with notifications here — the UI is strictly
 * for viewing messages and any associated images.</p>
 *
 * <p>The fragment performs these actions:</p>
 * <ul>
 *     <li>Initializes the session and database managers</li>
 *     <li>Creates an adapter and binds it to the ListView</li>
 *     <li>Triggers a database fetch to load administrator notifications</li>
 * </ul>
 */

public class NotificationsFragmentAdmin extends Fragment {
    /** Provides access to session-level state and managers. */
    private SessionManager SM;
    /** ViewBinding object for the administrator notifications layout. */
    private FragmentAdministratorsNotificationBinding binding;
    /**
     * Inflates the administrator notifications layout, initializes managers,
     * sets up the adapter, and triggers loading of admin notifications from
     * the database.
     *
     * @param inflater  The LayoutInflater used to inflate views
     * @param container Optional parent view group
     * @param savedInstanceState Previously saved state bundle, if any
     * @return The root view for this fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.d(TAG,"");
        binding = FragmentAdministratorsNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SM =  ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        IDatabase db = SM.getSession().getDatabase();

        ListView listView = binding.adminNotification;
        ArrayList<Notifications> notificationsList = new ArrayList<>();
        NotificationsAdminArrayAdapter adapter = new NotificationsAdminArrayAdapter(inflater.getContext(), notificationsList);
        listView.setAdapter(adapter);
        db.getNotificationAdmin( adapter,notificationsList);
    return root;
    }
}
