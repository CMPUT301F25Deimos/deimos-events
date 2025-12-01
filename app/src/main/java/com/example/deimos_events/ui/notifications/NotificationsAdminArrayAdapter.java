package com.example.deimos_events.ui.notifications;

import static android.view.View.GONE;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deimos_events.IDatabase;
import com.example.deimos_events.Notifications;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdminArrayAdapter extends ArrayAdapter<Notifications> {

    public NotificationsAdminArrayAdapter(Context context, List<Notifications> notifications) {
        super(context, 0, notifications);
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        Notifications notification = getItem(position);
        if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_and_button, parent, false);
        } else {
            view = convertView;
        }
        MaterialButton accept_button = view.findViewById(R.id.accept_button);
        MaterialButton decline_button = view.findViewById(R.id.decline_button);
        accept_button.setVisibility(GONE);
        decline_button.setVisibility(GONE);

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

