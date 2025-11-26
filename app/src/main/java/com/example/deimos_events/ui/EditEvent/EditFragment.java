package com.example.deimos_events.ui.EditEvent;

import static android.content.ContentValues.TAG;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.Event;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.ui.createEvent.createViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class EditFragment extends Fragment {

    private Button update;
    private ListView entrants;
    private ImageView image;
    private String eventId;
    private EventManager EM;
    private EditViewModel viewModel;
    private Button map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_and_change_image, container, false);
        map.findViewById(R.id.map);
        image = view.findViewById(R.id.imageView);
        Button update = view.findViewById(R.id.update);
        Button save = view.findViewById(R.id.saveButton);
        entrants = view.findViewById(R.id.listView);

        Bundle latLon = new Bundle();
        //if true bring up map
        map.setOnClickListener(v -> {
            Log.d(TAG, "test3");
            FrameLayout maps = view.findViewById(R.id.maps);
            view.findViewById(R.id.mapFragment).setVisibility(view.VISIBLE);
            update.setVisibility(view.GONE);
            save.setVisibility(view.GONE);
            map.setVisibility(view.GONE);

            SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            map.getMapAsync(googleMap -> {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5462,113.4937),1));
                googleMap.setOnMapClickListener(press ->{
                    double latitude = press.latitude;
                    double longitude = press.longitude;
                    String lat = String.valueOf(latitude);
                    String lon = String.valueOf(longitude);
                    latLon.putString("latitude", lat);
                    latLon.putString("longitude", lon);
                    view.findViewById(R.id.mapFragment).setVisibility(view.GONE);
                });
            });
        });
        super.onCreateView(inflater,container,savedInstanceState);
        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        this.EM = SM.getEventManager();
        Event event = SM.getSession().getCurrentEvent();
        final ActivityResultLauncher<String> pickImageLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                image.setImageURI(uri);
                            }

                        });
        update.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
                });

        viewModel = new ViewModelProvider(this).get(EditViewModel.class);

       save.setOnClickListener(v -> {
           Bitmap img =((BitmapDrawable)image.getDrawable()).getBitmap();
           String id = event.getId();
            EM.updateImage(id, img,callback ->{
//                if(callback){
//                    Log.d("TAG", "updated");
//                }
            });
         });


        eventId = getArguments().getString("eventId");



        byte[] decodedBytes = Base64.decode(event.getPosterId(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        image.setImageBitmap(bmp);

        EM.fetchAllRegistrations(event.getId(), regList ->{
            if (regList != null && !regList.isEmpty()){
                Log.d("TAG", "This is a debug message");
                    Log.d("TAG", "This is a debug message2");
                    EventAdapter adapter = new EventAdapter(getContext(), regList);
                    entrants.setAdapter(adapter);
                }

        });

        return view;
    }
}
