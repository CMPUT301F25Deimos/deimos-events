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
    /**
     * Creates a new adapter for displaying selectable tags in the
     * “Send Notifications” dropdown menu.
     *
     * @param context the context used to inflate the dropdown layout
     * @param tags    list of tag names shown in the dropdown
     */
    public SendNotificationsAdapter(@NonNull Context context, @NonNull List<String> tags) {
        super(context, 0, tags);
        inflater = LayoutInflater.from(context);
    }
    /**
     * Returns the view used to display a single dropdown item in the
     * notifications tag selector.
     *
     * <p>If an existing view can be reused, it will be recycled; otherwise,
     * a new view is inflated from {@code item_dropdown}.</p>
     *
     * @param position    the position of the item within the data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent view that this view will be attached to
     * @return the fully populated view for this dropdown item
     */
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