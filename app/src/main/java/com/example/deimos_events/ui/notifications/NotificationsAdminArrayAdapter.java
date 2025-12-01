package com.example.deimos_events.ui.notifications;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Registration;
import com.google.android.material.button.MaterialButton;

import java.util.List;
/**
 * Adapter used by administrators to display a list of {@link Notifications}
 * inside a ListView. Each row shows:
 * <ul>
 *     <li>The notification message</li>
 *     <li>A decoded image (if available)</li>
 *     <li>A hidden action button (admins do not interact here)</li>
 * </ul>
 *
 * <p>This adapter is strictly for viewing notifications; no interactive
 * actions are provided for admins.</p>
 */

public class NotificationsAdminArrayAdapter extends ArrayAdapter<Notifications> {
    /**
     * Creates a new adapter for displaying admin notifications.
     *
     * @param context       the context used for inflating layouts
     * @param notifications the list of notifications to render
     */
    public NotificationsAdminArrayAdapter(Context context, List<Notifications> notifications) {
        super(context, 0, notifications);
        /**
         * Provides the row view for a specific notification.
         *
         * <p>This method inflates the layout (if needed), hides the button,
         * sets the notification text, and decodes the Base64 image if present.</p>
         *
         * @param position    the position of the item in the list
         * @param convertView a recycled view (if available)
         * @param parent      the parent view group
         * @return the fully configured View for the row
         */
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        Log.d(TAG,"test here");
        Notifications notification = getItem(position);
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_and_button, parent, false);
        } else {
            view = convertView;
        }

        MaterialButton button = view.findViewById(R.id.placeholder_button);
        button.setVisibility(View.GONE);
        if (notification != null) {
            TextView textView = view.findViewById(R.id.event_text);
            textView.setText(notification.message);

            ImageView imageView = view.findViewById(R.id.event_image);
            String base64Image = notification.image;

            if (base64Image == null || base64Image.trim().isEmpty() || base64Image.equals("null")) {
                imageView.setImageResource(R.drawable.ic_events_black_24dp);
            } else {
                try {
                    if (base64Image.contains(",")) {
                        base64Image = base64Image.split(",")[1];
                    }
                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.ic_events_black_24dp);
                }
            }

        }
        return view;
    }
}
