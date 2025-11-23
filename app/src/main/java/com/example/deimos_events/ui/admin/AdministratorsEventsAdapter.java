package com.example.deimos_events.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.Event;
import com.example.deimos_events.R;

import java.util.ArrayList;
import java.util.List;

public class AdministratorsEventsAdapter
        extends RecyclerView.Adapter<AdministratorsEventsAdapter.EventViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Event event);
    }

    private final List<Event> events = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public AdministratorsEventsAdapter(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setEvents(List<Event> newEvents) {
        events.clear();
        if (newEvents != null) {
            events.addAll(newEvents);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.title.setText(event.getTitle() != null ? event.getTitle() : "Untitled event");
        holder.desc.setText(event.getDescription() != null ? event.getDescription() : "");

        String meta = "";
        if (event.getDate() != null) meta += event.getDate();
        if (event.getTime() != null) meta += meta.isEmpty() ? event.getTime() : " • " + event.getTime();
        if (event.getParticipantCap() != null) meta += " • Cap: " + event.getParticipantCap();
        if (event.getLocation() != null) meta += " • " + event.getLocation();

        holder.meta.setText(meta);

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        TextView meta;
        ImageButton deleteButton;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_admin_event_title);
            desc = itemView.findViewById(R.id.tv_admin_event_desc);
            meta = itemView.findViewById(R.id.tv_admin_event_meta);
            deleteButton = itemView.findViewById(R.id.btn_admin_delete_event);
        }
    }
}
