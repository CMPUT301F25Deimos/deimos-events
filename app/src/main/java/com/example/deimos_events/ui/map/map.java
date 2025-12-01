package com.example.deimos_events.ui.map;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends Fragment {
    FrameLayout maps;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"test2");
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        maps = view.findViewById(R.id.maps);
        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        EventManager EM = SM.getEventManager();
        Event event = SM.getSession().getCurrentEvent();
        ActorManager AM = SM.getActorManager();
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        map.getMapAsync(googleMap -> {
            googleMap.clear();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5462, 113.4937), 1));
            EM.fetchAllRegistrations(event.getId(), regList -> {
                if (regList != null && !regList.isEmpty()) {
                    for (Registration r : regList) {
                        Registration reg = r;
                        AM.actorExistsByid(reg.getEntrantId(), callback -> {
                            LatLng latlng = new LatLng(Double.valueOf(reg.getLatitude()), Double.valueOf(reg.getLongitude()));
                            MarkerOptions marker = new MarkerOptions()
                                    .position(latlng)
                                    .title(callback.getName());
                            Marker myMarker = googleMap.addMarker(marker);
                        });
                    }
                }

            });
        Log.d(TAG,"test");
    });
        return view;
}
}
