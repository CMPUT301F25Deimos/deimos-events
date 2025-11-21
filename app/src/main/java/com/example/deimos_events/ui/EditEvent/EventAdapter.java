package com.example.deimos_events.ui.EditEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Event;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.Session;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;

import java.util.List;
import java.util.function.Consumer;

public class EventAdapter extends ArrayAdapter<Registration> {
    public EventAdapter(Context context, List<Registration> register) {
            super(context, 0, (List<Registration>) register);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_view_and_change_image, parent, false);
        }
        Registration register = getItem(position);
        if (register != null) {
            TextView tv = convertView.findViewById(R.id.name);
            SessionManager SM = ((EventsApp) getContext().getApplicationContext()).getSessionManager();
            ActorManager AM = SM.getActorManager();
            AM.actorExistsByid(register.getEntrantId(), callback -> {
                tv.setText(callback.getName());
            });
            TextView status = convertView.findViewById(R.id.status);
            status.setText(register.getStatus());

            EventManager EM = SM.getEventManager();
            ImageButton btn = convertView.findViewById(R.id.x);
            btn.setOnClickListener(v -> {
                EM.deleteRegistration(register, callback -> {
                    Toast.makeText(getContext(), "Registration Deleted", Toast.LENGTH_SHORT).show();
                });
            });
        }

        return convertView;
    }


}

