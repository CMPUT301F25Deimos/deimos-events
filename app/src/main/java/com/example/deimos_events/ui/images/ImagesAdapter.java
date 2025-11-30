package com.example.deimos_events.ui.images;

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

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private final List<Event> events = new ArrayList<>();
    public ImagesAdapter(){}
    @NonNull
    @Override
    public ImagesAdapter.ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.images_admin_item,parent,false);
        return new ImagesAdapter.ImagesViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ImagesViewHolder holder, int position) {
        Event event = events.get(position);

        holder.title.setText(event.getTitle());
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
    public int getItemCount(){
        return events.size();
    }
    public void submitList(List<Event> list) {
        events.clear();
        if (list != null){
            for (Event event : list){
                String posterID = event.getPosterId();
                if(posterID != null){
                    events.add(event);
                }
            }

        }
        notifyDataSetChanged();
    }
    public void removeImage(int position) {
        if (position < 0 || position >= events.size()) return;
        events.remove(position);
        notifyItemRemoved(position);
    }

    public Event getEventAt(int position) {
        if (position < 0 || position >= events.size()) return null;
        return events.get(position);
    }

    static class ImagesViewHolder extends RecyclerView.ViewHolder {

        final ImageView image;
        final TextView title;


        ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.AdminImage);
            title = itemView.findViewById(R.id.ImageTitle);
        }
    }
}
