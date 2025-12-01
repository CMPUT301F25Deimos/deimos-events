package com.example.deimos_events.ui.EditEvent;

import android.content.Intent;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.MainActivity;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.ui.notifications.SendNotificationsFragment;
import com.example.deimos_events.ui.createEvent.CreateViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
/**
 * Fragment used for editing an existing {@link Event}. It allows organizers to:
 * <ul>
 *     <li>Update the event's poster image</li>
 *     <li>Navigate to a map view if location tracking is enabled</li>
 *     <li>Export entrant information as a CSV file</li>
 *     <li>Select additional content through the picker screen</li>
 *     <li>Open the notification dialog to send messages to entrants</li>
 * </ul>
 *
 * <p>The fragment loads the current event from the shared {@link SessionManager}
 * and updates the UI accordingly, including loading an existing poster image
 * if available. Organizers can then modify event details such as the poster,
 * or perform admin tasks like exporting CSV participant data.</p>
 *
 * <p>This fragment is always opened with a valid event stored in the session,
 * and requires the parent activity to provide an initialized {@link EventManager}.</p>
 */
public class EditFragment extends Fragment {

    private Button update;
    private ImageView image;
    private String eventId;
    private EventManager EM;
    private EditViewModel viewModel;
    private Button notify;
    private Button map;
    /**
     * Inflates and initializes the edit-event user interface.
     *
     * <p>This method performs several tasks:</p>
     * <ul>
     *     <li>Loads the current event from the {@link SessionManager}</li>
     *     <li>Displays the existing event poster image if available</li>
     *     <li>Sets up the map navigation button (if event uses geolocation)</li>
     *     <li>Registers an image picker for updating the event poster</li>
     *     <li>Initializes CSV export functionality for entrant lists</li>
     *     <li>Initializes “Send Notifications” dialog launching</li>
     * </ul>
     *
     * @param inflater  LayoutInflater used to inflate the layout
     * @param container Optional parent view
     * @param savedInstanceState previous saved state (unused)
     * @return The root view for this fragment
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_view_and_change_image, container, false);
        map = view.findViewById(R.id.map);
        image = view.findViewById(R.id.imageView);
        Button update = view.findViewById(R.id.update);
        Button save = view.findViewById(R.id.saveButton);
        view.findViewById(R.id.mapFragment).setVisibility(view.INVISIBLE);
        Button exportCsv = view.findViewById(R.id.exportCsvButton);
        Button not = view.findViewById(R.id.notify);
        Button pick = view.findViewById(R.id.pick);
        Button back = view.findViewById(R.id.back);
        SessionManager SM = ((EventsApp) getActivity().getApplication()).getSessionManager();
        ActorManager AM = SM.getActorManager();
        this.EM = SM.getEventManager();
        Event event = SM.getSession().getCurrentEvent();
        Bundle latLon = new Bundle();
        //if true bring up map
        map.setOnClickListener(v -> {
            if (event.getRecordLocation()) {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_editFragment_to_mapFragment);

                FrameLayout maps = view.findViewById(R.id.maps);
                view.findViewById(R.id.mapFragment).setVisibility(view.VISIBLE);
                update.setVisibility(view.GONE);
                save.setVisibility(view.GONE);
                map.setVisibility(view.GONE);
                exportCsv.setVisibility(view.GONE);
                not.setVisibility(view.GONE);
                pick.setVisibility(view.GONE);
                back.setVisibility(view.VISIBLE);

            } else {
                Toast.makeText(getContext(), "Location not enabled", Toast.LENGTH_SHORT);
            }
        });
        back.setOnClickListener(v -> {
            view.findViewById(R.id.mapFragment).setVisibility(view.GONE);
            update.setVisibility(view.VISIBLE);
            save.setVisibility(view.VISIBLE);
            map.setVisibility(view.VISIBLE);
            exportCsv.setVisibility(view.VISIBLE);
            not.setVisibility(view.VISIBLE);
            pick.setVisibility(view.VISIBLE);
            back.setVisibility(view.INVISIBLE);

        });
        super.onCreateView(inflater, container, savedInstanceState);


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
            Bitmap img = ((BitmapDrawable) image.getDrawable()).getBitmap();
            String id = event.getId();
            EM.updateImage(id, img, callback -> {
            });
        });

        eventId = getArguments().getString("eventId");

        exportCsv.setOnClickListener(v -> {
            EM.exportEntrantsCsv(event.getId(), csvData -> {
                if (csvData == null) {
                    Toast.makeText(getContext(), "No entrants to export!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String filename = "entrants_" + event.getId() + ".csv";
                    File file = new File(getContext().getExternalFilesDir(null), filename);

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(csvData.getBytes());
                    fos.close();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND).setType("text/csv");

                    Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(shareIntent, "Export CSV"));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error exporting CSV", Toast.LENGTH_SHORT).show();
                }
            });
        });

        pick.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.navigation_picker);
        });
        if(event.getPosterId()!= null) {
            byte[] decodedBytes = Base64.decode(event.getPosterId(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            image.setImageBitmap(bmp);
        }

        notify = view.findViewById(R.id.notify);
        notify.setOnClickListener(v -> {
            SM.getSession().setCurrentEvent(event);

            SendNotificationsFragment dialog = new SendNotificationsFragment();
            dialog.show(getParentFragmentManager(), "MessageDialog");
        });

        return view;
    }
}
