package com.example.deimos_events.ui.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.R;

import java.util.ArrayList;
import java.util.List;
/**
 * RecyclerView adapter for displaying a list of {@link Actor} objects.
 * <p>
 * Each row shows the user's profile image, name, and role.
 * The adapter supports submitting a full list, retrieving individual items,
 * and removing an item by position.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    /** Internal list of actors to display in the RecyclerView. */
    private final List<Actor> actors = new ArrayList<>();

    /** Default constructor for the adapter. */
    public UsersAdapter() {}

    /**
     * Inflates the user list item layout and creates a ViewHolder.
     *
     * @param parent   The parent ViewGroup into which the new View will be inserted.
     * @param viewType The type of the new view.
     * @return A new {@link UserViewHolder} instance.
     */
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item,parent,false);
        return new UserViewHolder(v);
    }
    /**
     * Binds an {@link Actor} object to a row in the RecyclerView.
     *
     * @param holder   The ViewHolder containing the row views.
     * @param position The index of the item to bind.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Actor actor = actors.get(position);
        holder.name.setText(actor.getName());
        holder.role.setText(actor.getRole());
    }
    /**
     * Returns the number of actors currently displayed in the adapter.
     *
     * @return The number of items.
     */
    @Override
    public int getItemCount(){
        return actors.size();
    }
    /**
     * Replaces the current list of actors with the given list
     * and refreshes the RecyclerView.
     *
     * @param list A list of {@link Actor} objects to display.
     */
    public void submitList(List<Actor> list) {
        actors.clear();
        if (list != null) actors.addAll(list);
        notifyDataSetChanged();
    }
    /**
     * Retrieves the Actor at a specific position.
     *
     * @param position The index of the item to retrieve.
     * @return The corresponding {@link Actor}, or null if out of range.
     */
    public Actor getActorAt(int position) {
        if (position < 0 || position >= actors.size()) return null;
        return actors.get(position);
    }
    /**
     * Removes the actor at the given position and updates the UI.
     *
     * @param position The index of the item to remove.
     */
    public void removeAt(int position) {
        if (position < 0 || position >= actors.size()) return;
        actors.remove(position);
        notifyItemRemoved(position);
    }
    /**
     * ViewHolder representing an individual user row in the RecyclerView.
     * Holds references to the profile image, name, and role text views.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {

        /** ImageView for the user's profile picture. */
        final ImageView profile;

        /** TextView displaying the user's name. */
        final TextView name;
        /** TextView displaying the user's role (Entrant, Organizer, Admin, etc.). */
        final TextView role;
        /**
         * Creates a new ViewHolder bound to the user_list_item layout.
         *
         * @param itemView The inflated row view.
         */
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.userProfileImage);
            name = itemView.findViewById(R.id.userName);
            role = itemView.findViewById(R.id.userRole);
        }
    }
}
