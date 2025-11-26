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

import com.example.deimos_events.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.model.LatLng;

public class map extends Fragment {
    FrameLayout maps;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"test2");
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        maps = view.findViewById(R.id.maps);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);

        map.getMapAsync(googleMap -> {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5462,113.4937),1));
            googleMap.setOnMapClickListener(press ->{
                double latitude = press.latitude;
                double longitude = press.longitude;
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);
                Bundle arg = new Bundle();
                arg.putString("latitude", lat);
                arg.putString("longitude", lon);
                view.findViewById(R.id.mapFragment).setVisibility(view.INVISIBLE);
            });
    });
        Log.d(TAG,"test");
        return view;
    }
}
