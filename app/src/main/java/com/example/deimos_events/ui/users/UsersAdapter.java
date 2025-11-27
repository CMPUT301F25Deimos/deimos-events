package com.example.deimos_events.ui.users;

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

import com.example.deimos_events.Actor;
import com.example.deimos_events.Organizer;
import com.example.deimos_events.R;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private final List<Actor> actors = new ArrayList<>();
    public UsersAdapter(){}
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.user_list_item,parent,false);
        return new UserViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Actor actor = actors.get(position);

        holder.name.setText(actor.getName());

        holder.role.setText(actor.getRole());


    }
    @Override
    public int getItemCount(){
        return actors.size();
    }
    public void submitList(List<Actor> list) {
        actors.clear();
        if (list != null) actors.addAll(list);
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        final ImageView profile;
        final TextView name;
        final TextView role;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.userProfileImage);
            name = itemView.findViewById(R.id.userName);
            role = itemView.findViewById(R.id.userRole);
        }
    }
}
