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
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.SessionManager;

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

        // Initialize image picker
        final ActivityResultLauncher<String> pickImageLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                image.setImageURI(uri);
                            }
                        });

        // Get Session Manager safely
        EventsApp app = (EventsApp) requireActivity().getApplication();
        SessionManager SM = app != null ? app.getSessionManager() : null;
        EM = SM != null ? SM.getEventManager() : null;

        // Get event ID safely
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("id");
        }

        if (eventId == null || EM == null) {
            Log.e("EditFragment", "Missing eventId or EventManager — cannot load image.");
            return view;
        }

        // Load image from database
        EM.getImage(eventId, event -> {
            if (event == null || event.getPosterId() == null) {
                Log.e("EditFragment", "No image data found for eventId: " + eventId);
                return;
            }
            try {
                String poster = event.getPosterId();
                byte[] decodedBytes = Base64.decode(poster, Base64.DEFAULT);
                Bitmap img = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                requireActivity().runOnUiThread(() -> image.setImageBitmap(img));
                Log.d(TAG,"test");
            } catch (Exception e) {
                Log.e("EditFragment", "Error decoding image: " + e.getMessage(), e);
            }
        });



        // Set up update button
        update.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        // Optional: another button or trigger to actually upload the new image
        // This avoids null crashes if updateImage runs before a picture is chosen
        view.findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (image.getDrawable() != null) {
                Bitmap imageBit = ((BitmapDrawable) image.getDrawable()).getBitmap();
                EM.updateImage(eventId, imageBit);
                Log.d("EditFragment", "Image updated for eventId: " + eventId);
            } else {
                Log.w("EditFragment", "No image selected to update.");
            }
        });

        return view;
    }
}
