package com.example.deimos_events.ui.events;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.deimos_events.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<EventTest>{
    
    public EventArrayAdapter(Context context, ArrayList<EventTest> events) {
        super(context, 0, events);
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
        
        EventTest event = getItem(position);
        
        if (event != null) {
            TextView textView = view.findViewById(R.id.event_text);
            textView.setText(event.description);
            
            ImageView imageView = view.findViewById(R.id.event_image);
            imageView.setImageResource(event.image);
            
            Boolean own = event.ownEvent;
            clicked(button, event.waitingList, own);

            button.setOnClickListener(v -> {
                event.setWaitingList(!event.getWaitingList());
                clicked(button, event.getWaitingList(), own);
            });
        }
        
        return view;
    }
    
    private void clicked(MaterialButton button, boolean click, boolean own) {
        // sets icon button depending on clicked state
        Drawable icon_button;
        ColorStateList button_colour;
        
        // sees if the organizer owns the event (thus, will have an edit button)
        if (!own) {
            // sees if already part of waiting list
            if (!click) {
                icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.join_sticker_24dp);
                button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.chosen_background);
                button.setText("Join");
            } else {
                icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.cancel_24dp);
                button_colour = ContextCompat.getColorStateList(this.getContext(), com.google.android.material.R.color.design_default_color_error);
                button.setText("Cancel");
            }
        }
        else {
            icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.baseline_edit_24);
            button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.chosen_background);
            button.setText("Edit");
        }
        button.setIcon(icon_button);
        button.setBackgroundTintList(button_colour);
    }
}
