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
/**
 * RecyclerView adapter used by administrators to view a list of all events.
 *
 * <p>This adapter is responsible for:</p>
 * <ul>
 *     <li>Displaying event title, description, metadata, and poster image</li>
 *     <li>Handling Base64 decoding of poster images</li>
 *     <li>Updating the displayed list of events</li>
 *     <li>Providing helper functions to remove events and retrieve items</li>
 * </ul>
 *
 * <p>Each event item is rendered using {@code item_admin_event.xml}.</p>
 */
public class AdministratorsEventsAdapter
        extends RecyclerView.Adapter<AdministratorsEventsAdapter.AdminEventViewHolder> {
    /** Internal list storing the events shown in the RecyclerView. */
    private final List<Event> events = new ArrayList<>();
    /** Default constructor. */
    public AdministratorsEventsAdapter() {
    }
    /**
     * Inflates the layout for a single event item and returns a ViewHolder.
     *
     * @param parent   the parent view group
     * @param viewType unused viewType parameter
     * @return a new {@link AdminEventViewHolder}
     */
    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new AdminEventViewHolder(v);
    }
    /**
     * Binds event data to the ViewHolder's views.
     *
     * <p>Responsibilities:</p>
     * <ul>
     *     <li>Sets title, description, and metadata text</li>
     *     <li>Formats metadata string containing deadline, capacity, and location flag</li>
     *     <li>Decodes Base64 posterId into a Bitmap and displays it in the ImageView</li>
     * </ul>
     *
     * @param holder   the ViewHolder to bind data into
     * @param position the index of the event in the internal list
     */

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
    /**
     * Returns the number of events currently displayed.
     * @return the event count
     */
    @Override
    public int getItemCount() {
        return events.size();
    }
    /**
     * Replaces the current event list with the given list and refreshes the UI.
     *
     * @param list the new list of {@link Event} objects
     */
    public void submitList(List<Event> list) {
        events.clear();
        if (list != null) {
            events.addAll(list);
        }
        notifyDataSetChanged();
    }
    /**
     * Removes an event at the specified position and updates the RecyclerView.
     * @param position the index of the event to remove
     */
    public void removeEvent(int position) {
        if (position < 0 || position >= events.size()) return;
        events.remove(position);
        notifyItemRemoved(position);
    }
    /**
     * Returns the event located at the given position.
     *
     * @param position index of the desired event
     * @return the {@link Event} at the position, or {@code null} if out of bounds
     */
    public Event getEventAt(int position) {
        if (position < 0 || position >= events.size()) return null;
        return events.get(position);
    }
    /**
     * ViewHolder class representing a single event item within the admin event list.
     *
     * <p>Holds references to UI components for fast binding.</p>
     */
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
