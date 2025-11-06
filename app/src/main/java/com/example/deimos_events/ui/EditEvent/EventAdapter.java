package com.example.deimos_events.ui.EditEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deimos_events.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<String> {
    public EventAdapter(Context context, Register register) {
        super(context, 0, register);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_view_and_change_image, parent, false);

        String Name = getItem(position);
        TextView tv = convertView.findViewById(R.id.name);
        tv.setText(Name);

        ImageButton btn = convertView.findViewById(R.id.x);
        btn.setOnClickListener(v -> {

        });

        return convertView;
    }
}

