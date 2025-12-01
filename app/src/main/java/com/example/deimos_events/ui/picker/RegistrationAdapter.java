package com.example.deimos_events.ui.picker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.List;

public class RegistrationAdapter extends ArrayAdapter<Registration> {

    public interface OnDeleteListener {
        void onDelete(String status);
    }

    private OnDeleteListener listener;

    public RegistrationAdapter(@NonNull Context context, @NonNull List<Registration> registrations, @Nullable OnDeleteListener listener) {
        super(context, 0, registrations);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.entrant_view_and_x, parent, false);
        }

        SessionManager SM = ((EventsApp) getContext().getApplicationContext()).getSessionManager();
        EventManager EM = SM.getEventManager();
        Event event = SM.getSession().getCurrentEvent();

        Registration registration = getItem(position);
        if (registration != null) {
            TextView nameView = convertView.findViewById(R.id.name);
            TextView statusView = convertView.findViewById(R.id.status);
            ImageButton deleteButton = convertView.findViewById(R.id.x);

            EM.getActorById(registration.getEntrantId(), actor -> {
                nameView.setText(actor.getName());
                statusView.setText(registration.getStatus());

                if ("Pending".equals(registration.getStatus())) {
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(v -> {
                        EM.deleteRegistration(registration.getId(), callback -> {
                            Toast.makeText(getContext(), "Registration Deleted", Toast.LENGTH_SHORT).show();

                            remove(registration);
                            notifyDataSetChanged();

                            if (listener != null) listener.onDelete(registration.getStatus());
                        });
                    });
                } else {
                    deleteButton.setVisibility(View.GONE);
                }
            });
        }

        return convertView;
    }
}
