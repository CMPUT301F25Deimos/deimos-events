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

public class NotificationsFragmentAdmin extends Fragment {

    private SessionManager SM;
    private FragmentAdministratorsNotificationBinding binding;

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
