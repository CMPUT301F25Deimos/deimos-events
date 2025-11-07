package com.example.deimos_events.ui.EditEvent;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.SessionManager;

import java.util.List;

public class EditFragment extends Fragment {

    private Button update;
    private ListView entrants;
    private ImageView image;
    private String eventId;
    private EventManager EM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_and_change_image, container, false);
        update = view.findViewById(R.id.update);
        entrants = view.findViewById(R.id.listView);
        image = view.findViewById(R.id.imageView);
        eventId = getArguments().getString("eventId");
        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        EventManager EM = SM.getEventManager();
        List< Registration > registrations = EM.getRegistration(eventId);
        for(Registration r : registrations) {
            EventAdapter adapter = new EventAdapter(getContext(), r);
            entrants.setAdapter(adapter);
        }
        return view;
    }
}
