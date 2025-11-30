package com.example.deimos_events.ui.events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.R;

import java.util.ArrayList;
import java.util.List;


public class AdministratorsEventsAdapter
        extends RecyclerView.Adapter<AdministratorsEventsAdapter.AdminEventViewHolder> {

    private final List<Event> events = new ArrayList<>();

    public AdministratorsEventsAdapter() {
    }

    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new AdminEventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.title.setText(event.getTitle());
        holder.desc.setText(event.getDescription());

        String meta = "";
        try {
            meta = event.getRegistrationDeadline()
                    + " • Cap: " + event.getParticipantCap()
                    + " • Location: " + event.getRecordLocation();
        } catch (Exception ignored) {}
        holder.meta.setText(meta);

        // Decode Base64 posterId to Bitmap for the thumbnail
        String base64Image = event.getPosterId();
        if (base64Image != null && !base64Image.trim().isEmpty() && !"0".equals(base64Image)) {
            try {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.image.setImageBitmap(bmp);
            } catch (Exception e) {
                holder.image.setImageDrawable(null);
            }
        } else {
            holder.image.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void submitList(List<Event> list) {
        events.clear();
        if (list != null) {
            events.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void removeEvent(int position) {
        if (position < 0 || position >= events.size()) return;
        events.remove(position);
        notifyItemRemoved(position);
    }

    public Event getEventAt(int position) {
        if (position < 0 || position >= events.size()) return null;
        return events.get(position);
    }

    static class AdminEventViewHolder extends RecyclerView.ViewHolder {

        final ImageView image;
        final TextView title;
        final TextView desc;
        final TextView meta;

        AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_admin_event_image);
            title = itemView.findViewById(R.id.tv_admin_event_title);
            desc = itemView.findViewById(R.id.tv_admin_event_desc);
            meta = itemView.findViewById(R.id.tv_admin_event_meta);
        }
    }
}
