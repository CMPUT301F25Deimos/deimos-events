package com.example.deimos_events.ui.EditEvent;

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

public class EditFragment extends Fragment {

    private Button update;
    private ListView entrants;
    private ImageView image;
    private String eventId;
    private EventManager EM;
    private EditViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_and_change_image, container, false);
        super.onCreateView(inflater,container,savedInstanceState);
        image = view.findViewById(R.id.imageView);
        Button update = view.findViewById(R.id.update);
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
        Button save = view.findViewById(R.id.saveButton);
       save.setOnClickListener(v -> {
           Bitmap img =((BitmapDrawable)image.getDrawable()).getBitmap();
           String id = event.getId();
            EM.updateImage(id, img,callback ->{
//                if(callback){
//                    Log.d("TAG", "updated");
//                }
            });
         });
        entrants = view.findViewById(R.id.listView);

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
