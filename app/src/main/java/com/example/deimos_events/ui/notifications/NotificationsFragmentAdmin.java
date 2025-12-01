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
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentNotificationsBinding;
import com.example.deimos_events.managers.NotificationManager;
import com.example.deimos_events.managers.SessionManager;

public class NotificationsFragmentAdmin extends Fragment {

    private SessionManager SM;
    private String orgId;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.d(TAG,"test5");
        requireActivity().setTitle(savedInstanceState.get("actorName").toString());
        View view = inflater.inflate(R.layout.fragment_notifications, container,false);
        SM =  ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        NotificationManager NM = SM.getNotificationManager();
        ListView listView = view.findViewById(R.id.notifications_list);
        NM.getNotification(savedInstanceState.get("orgId").toString(), callback->{
            NotificationsAdminArrayAdapter adapter = new NotificationsAdminArrayAdapter(inflater.getContext(), callback);
            listView.setAdapter(adapter);
        });
    return view;
    }
}
