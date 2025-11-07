package com.example.deimos_events.ui.EditEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deimos_events.ActorManager;
import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.Session;
import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Registration> {
    public EventAdapter(Context context, Registration register) {
        super(context, 0, (List<Registration>) register);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_view_and_change_image, parent, false);

        Registration register = getItem(position);
        if (register == null)
            return convertView;
        TextView tv = convertView.findViewById(R.id.name);
        String Name = register.getEntrantId();
        SessionManager SM = ((EventsApp)getContext().getApplicationContext()).getSessionManager();
        ActorManager AM = SM.getActorManager();
        UserInterfaceManager UIM = SM.getUserInterfaceManager();
        EventManager EM = SM.getEventManager();
        Session session = SM.getSession();
        // going to assume you mean the current entrant
        // in this case call the session object

        AM.fetchActorByID(Name,callback->{
            if (callback.isSuccess()){
                tv.setText(UIM.getCurrentActor().getName());
            }
        });
        TextView status = convertView.findViewById(R.id.status);
        status.setText(register.getStatus());

        ImageButton btn = convertView.findViewById(R.id.x);
        btn.setOnClickListener(v -> {
        EM.deleteRegistration(register, callback ->{
            Toast.makeText(getContext(), "Registration Deleted", Toast.LENGTH_SHORT).show();
        });
        });

        return convertView;
    }


}

