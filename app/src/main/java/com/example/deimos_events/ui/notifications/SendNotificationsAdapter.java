package com.example.deimos_events.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deimos_events.R;

import java.util.List;

/**
 * ArrayAdapter for the dropdown for sending notifications
 * Basically, the adapter for the people that the sender can send to
 */
public class SendNotificationsAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;
    
    public SendNotificationsAdapter(@NonNull Context context, @NonNull List<String> tags) {
        super(context, 0, tags);
        inflater = LayoutInflater.from(context);
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_dropdown, parent, false);
        }
        
        TextView textView = convertView.findViewById(R.id.tag_text);
        textView.setText(getItem(position));
        
        return convertView;
    }
}