package com.example.deimos_events.ui.events;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter for displaying Event objects in a ListView.
 * Handles normal entrant/organizer event lists and entrant history lists.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    private Set<String> registeredEventIds;
    private final Actor actor;
    private final SessionManager sm;
    private final EventManager EM;

    private final ActorManager AM;
    private final NavController navControl;

    /** Mapping of eventId -> registration status string */
    private final Map<String, String> registrationStatuses;

    /** If true, join/cancel/edit button is hidden and status text is shown */
    private final boolean historyMode;

    /**
     * Constructor for default mode (no history, no statuses).
     *
     * @param context Adapter context
     * @param events List of events
     * @param registeredEventIds Events the entrant is registered in
     * @param sm Session manager
     * @param actor Current actor
     * @param navController Navigation controller
     */
    public EventArrayAdapter(Context context,
                             List<Event> events,
                             Set<String> registeredEventIds,
                             SessionManager sm,
                             Actor actor,
                             NavController navController) {
        this(context, events, registeredEventIds,
                null,
                false,
                sm, actor, navController);
    }

    /**
     * Constructor used when loading entrant event history.
     *
     * @param context Adapter context
     * @param events List of events
     * @param registeredEventIds Joined event IDs
     * @param registrationStatuses Map of eventId -> status
     * @param historyMode Whether history mode is active
     * @param sm Session manager
     * @param actor Current actor
     * @param navController Navigation controller
     */
    public EventArrayAdapter(Context context,
                             List<Event> events,
                             Set<String> registeredEventIds,
                             @Nullable Map<String, String> registrationStatuses,
                             boolean historyMode,
                             SessionManager sm,
                             Actor actor,
                             NavController navController) {
        super(context, 0, events);

        this.sm = sm;
        this.AM = sm.getActorManager();
        this.EM = sm.getEventManager();
        this.registeredEventIds = new HashSet<>(registeredEventIds);
        this.registrationStatuses = (registrationStatuses != null)
                ? registrationStatuses
                : new HashMap<>();
        this.historyMode = historyMode;
        this.actor = actor;
        this.navControl = navController;
    }

    /**
     * Populates each row of the ListView with event information.
     */
    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        View view = (convertView == null)
                ? LayoutInflater.from(getContext())
                .inflate(R.layout.listview_content_and_button, parent, false)
                : convertView;

        MaterialButton button = view.findViewById(R.id.placeholder_button);

        if (historyMode) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }

        IDatabase db = sm.getSession().getDatabase();
        Session session = sm.getSession();
        Event event = getItem(position);

        if (event != null) {

            TextView description = view.findViewById(R.id.event_text);
            description.setText(event.getDescription());

            ImageView imageView = view.findViewById(R.id.event_image);
            String base64Image = event.getPosterId();
            if (base64Image != null
                    && !base64Image.trim().isEmpty()
                    && !base64Image.equals("0")) {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bmp);
            }

            TextView statusText = view.findViewById(R.id.event_status);
            if (statusText != null) {
                if (!historyMode) {
                    statusText.setVisibility(View.GONE);
                } else {
                    String rawStatus = registrationStatuses.get(event.getId());
                    if (rawStatus == null) {
                        statusText.setVisibility(View.GONE);
                    } else {
                        statusText.setVisibility(View.VISIBLE);

                        String norm = rawStatus.toUpperCase();
                        String display;
                        switch (norm) {
                            case "ACCEPTED":
                            case "SELECTED":
                                display = "Status: Accepted";
                                break;
                            case "DECLINED":
                            case "REJECTED":
                            case "NOT_SELECTED":
                                display = "Status: Declined";
                                break;
                            case "WAITLISTED":
                                display = "Status: Waitlisted";
                                break;
                            case "PENDING":
                                display = "Status: Pending";
                                break;
                            default:
                                display = "Status: " + rawStatus;
                        }
                        statusText.setText(display);
                    }
                }
            }

            if (!historyMode) {
                changeButtonLook(button, event);

                button.setOnClickListener(v -> {
                    if (!actor.getDeviceIdentifier().equals(event.getOwnerId())) {
                        Log.d(TAG, actor.getDeviceIdentifier() + " ownerID" + event.getOwnerId());
                        if (registeredEventIds.contains(event.getId())) {
                            registeredEventIds.remove(event.getId());
                            EM.leaveEvent(event.getId(), actor, result -> {
                                if (!result.isSuccess()){
                                    Toast.makeText(v.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            registeredEventIds.add(event.getId());
                            EM.joinEvent(getContext(), event.getId(), actor, result ->{
                                if (!result.isSuccess()) {
                                    Toast.makeText(v.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.navigation_organizers_events, false)
                                .build();
                        Bundle arg = new Bundle();
                        arg.putString("id", event.getId());
                        sm.getSession().setCurrentEvent(event);
                        navControl.navigate(R.id.navigation_edit, arg, navOptions);
                    }

                    changeButtonLook(button, event);
                });
            } else {
                button.setOnClickListener(null);
            }
        }

        view.setOnClickListener(v -> {
            sm.getSession().setCurrentEvent(event);
            AM.fetchActorRole(session.getCurrentActor(), result -> {
                if (!result.isSuccess()) {
                    // Optional fallback
                    Toast.makeText(v.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String role = result.getMessage();

                if ("Organizer".equals(role)) {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_navigation_organizers_events_to_navigation_event_info);
                } else if ("Entrant".equals(role)) {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_navigation_entrants_events_to_navigation_event_info);
                }
            });
        });

        return view;
    }

    /**
     * Updates the appearance of the join/cancel/edit button for an event row.
     *
     * @param button MaterialButton to modify
     * @param event  Event associated with this row
     */
    private void changeButtonLook(MaterialButton button, Event event) {
        Drawable icon_button;
        ColorStateList button_colour;

        int colour, icon;

        boolean ownsEvent = actor.getDeviceIdentifier().equals(event.getOwnerId());
        boolean hasJoined = registeredEventIds.contains(event.getId());

        if (ownsEvent) {
            button.setText("Edit");
            colour = R.color.title_colour;
            icon = R.drawable.baseline_edit_24;
        } else {
            button.setText(hasJoined ? "Cancel" : "Join");
            colour = hasJoined
                    ? com.google.android.material.R.color.design_default_color_error
                    : R.color.title_colour;
            icon = hasJoined ? R.drawable.cancel_24dp : R.drawable.join_sticker_24dp;
        }

        icon_button = ContextCompat.getDrawable(this.getContext(), icon);
        button_colour = ContextCompat.getColorStateList(this.getContext(), colour);

        button.setIcon(icon_button);
        button.setBackgroundTintList(button_colour);
    }

    /**
     * Updates the set of joined event IDs and refreshes the ListView.
     *
     * @param newJoinedEventIds New set of event IDs the entrant has joined
     */
    public void updateJoinedEvents(Set<String> newJoinedEventIds) {
        this.registeredEventIds = new HashSet<>(newJoinedEventIds);
        notifyDataSetChanged();
    }
}
