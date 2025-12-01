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
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdminArrayAdapter extends ArrayAdapter<String> {

    public NotificationsAdminArrayAdapter(Context context, List<String> Description) {
        super(context, 0, Description);
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        String notification = getItem(position);
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
            textView.setText(notification);
            ImageView imageView = view.findViewById(R.id.event_image);
            imageView.setVisibility(GONE);
        }
        return view;
    }
}
