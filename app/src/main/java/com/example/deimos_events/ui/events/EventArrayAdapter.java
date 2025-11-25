package com.example.deimos_events.ui.events;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaDrm;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.Actor;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.Event;
import com.example.deimos_events.MainActivity;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Array adapter for the notifications
 * - Designs buttons depending on whether user owns the event, and whether they have or have not joined the waiting list for an event
 * - TODO: As of right now, user cannot yet access the edit page by use of the edit button
 */
public class EventArrayAdapter extends ArrayAdapter<Event>{
    private Set<String> registeredEventIds;
    private final Actor actor;
    private final SessionManager sm;
    private final NavController navControl;

    
    public EventArrayAdapter(Context context, List<Event> events,
                             Set<String> registeredEventIds, SessionManager sm, Actor actor, NavController navController ) {
        super(context, 0, events);

        this.sm = sm;
        this.registeredEventIds = new HashSet<>(registeredEventIds);
        this.actor = actor;
        this.navControl = navController;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_and_button, parent, false);
        } else {
            view = convertView;
        }
        
        MaterialButton button = view.findViewById(R.id.placeholder_button);
        
        IDatabase db = sm.getSession().getDatabase();
        
        Event event = getItem(position);
        
        if (event != null) {
            // gets event images and descriptions
            TextView description = view.findViewById(R.id.event_text);
            description.setText(event.getDescription());
            
            ImageView imageView = view.findViewById(R.id.event_image);
            
            // turns base64 image to bitmap to be able to show it
            String base64Image = event.getPosterId();
            // makes sure it's a valid image
            if (event.getPosterId() == null || event.getPosterId().trim().isEmpty() || event.getPosterId().equals("0")) return view;
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(bmp);
            
            // changes buttons initial look
            changeButtonLook(button, event);
            
            button.setOnClickListener(v -> {
                // sees if the current user owns the event or not
                if (!actor.getDeviceIdentifier().equals(event.getOwnerId())) {
                    Log.d(TAG, actor.getDeviceIdentifier() +" ownerID"  + event.getOwnerId());
                    if (registeredEventIds.contains(event.getId())) {
                        registeredEventIds.remove(event.getId());
                        db.leaveEvent(event.getId(), actor);
                    } else {
                        registeredEventIds.add(event.getId());
                        db.joinEvent(event.getId(), actor);
                    }
                }else{
                    Log.d(TAG, "test");
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_organizers_events, true).build();
                    Bundle arg = new Bundle();
                    arg.putString("id", event.getId());
                    sm.getSession().setCurrentEvent(event);
                    navControl.navigate(R.id.navigation_edit, arg, navOptions);
                }
                
                changeButtonLook(button, event);
            });
        }
        
        // if clicking a Listview item, you select an event
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.getSession().setCurrentEvent(event);
                Navigation.findNavController(v).navigate(R.id.action_navigation_organizers_events_to_navigation_event_info);
            }
        });
        
        return view;
    }
    
    private void changeButtonLook(MaterialButton button, Event event) {
        // sets icon button depending on clicked state
        Drawable icon_button;
        ColorStateList button_colour;
        
        int colour, icon;
        
        // checks whether user owns or has joined the event to customize the buttons
        boolean ownsEvent = actor.getDeviceIdentifier().equals(event.getOwnerId());
        boolean hasJoined = registeredEventIds.contains(event.getId());
        
        if (ownsEvent) {
            button.setText("Edit");
            colour = R.color.title_colour;
            icon = R.drawable.baseline_edit_24;
        } else {
            // if user has joined the event, then they have the option to cancel, otherwise, they have the chance to join
            button.setText(hasJoined ? "Cancel" : "Join");
            colour = hasJoined ? com.google.android.material.R.color.design_default_color_error : R.color.title_colour;
            icon = hasJoined ? R.drawable.cancel_24dp : R.drawable.join_sticker_24dp;
        }
        
        // updates the icon and colour depending on what the state of the buttons are
        icon_button = ContextCompat.getDrawable(this.getContext(), icon);
        button_colour = ContextCompat.getColorStateList(this.getContext(), colour);
        
        button.setIcon(icon_button);
        button.setBackgroundTintList(button_colour);
    }
    
    /**
     * updates events list with joined or left events
     * @param newJoinedEventIds
     */
    public void updateJoinedEvents(Set<String> newJoinedEventIds) {
        this.registeredEventIds = new HashSet<>(newJoinedEventIds);
        notifyDataSetChanged();
    }
    
}
