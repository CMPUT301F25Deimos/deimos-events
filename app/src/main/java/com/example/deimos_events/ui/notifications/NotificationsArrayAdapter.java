package com.example.deimos_events.ui.notifications;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Array adapter for the notifications
 * - Designs buttons depending on whether user was accepted, or if they accepted/declined their offer
 * - TODO: As of right now, user automatically gets a notification right after entering an event because
 * - TODO: event lottery logic has yet to be implemented
 */
public class NotificationsArrayAdapter extends ArrayAdapter<Registration>{
    private IDatabase db;
    public NotificationsArrayAdapter(Context context, ArrayList<Registration> events, IDatabase db) {
        super(context, 0, events);
        this.db = db;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        
        Registration notification = getItem(position);
        
        
        if (convertView == null) {
            // sees whether user was kept as a waitlist, or chosen by lottery
            if (notification.getStatus() != "Not Selected") {
                view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_and_splitbutton, parent, false);
            }
            else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_and_button, parent, false);
            }
        } else {
            view = convertView;
        }
        
        
        if (notification != null) {
            TextView textView = view.findViewById(R.id.event_text);
            textView.setText(notification.description);
            
            ImageView imageView = view.findViewById(R.id.event_image);
            String base64Image = notification.image.contains(",") ? notification.image.split(",")[1] : notification.image;
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
            
            // person is not accepted, shows option to be removed from waiting list
            if (notification.getStatus() == "Not Selected") {
                MaterialButton button = view.findViewById(R.id.placeholder_button);
                Drawable icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.cancel_24dp);
                ColorStateList button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.decline_red);
                button.setText("Cancel");
                button.setIcon(icon_button);
                button.setBackgroundTintList(button_colour);
                db.answerEvent(notification.getId(), "Accepted");
            }
            // gives person the choices to accept/decline offer
            else {
                MaterialButtonToggleGroup waitingListAnswer = view.findViewById(R.id.split_button_layout);
                MaterialButton accept_button = view.findViewById(R.id.accept_button);
                MaterialButton decline_button = view.findViewById(R.id.decline_button);
                
                // changes accept/decline button roundness so that they appear as they originally looked like
                ShapeAppearanceModel original_accept = accept_button.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, 15f)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 15f)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                        .setTopRightCorner(CornerFamily.ROUNDED, 0f)
                        .build();
                
                ShapeAppearanceModel original_decline = decline_button.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 35f)
                        .setTopRightCorner(CornerFamily.ROUNDED, 35f)
                        .build();
                
                
                if ("Accepted".equals(notification.getStatus())) {
                    accept_button.setCornerRadius(100);
                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.accept_green));
                    
                    // Reset decline button
                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                    decline_button.setShapeAppearanceModel(original_decline);
                    
                } else if ("Declined".equals(notification.getStatus())) {
                    decline_button.setCornerRadius(100);
                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.decline_red));
                    
                    // Reset accept button
                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                    accept_button.setShapeAppearanceModel(original_accept);
                } else {
                    // Default colors if not answered yet
                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                }
                
                
                
                waitingListAnswer.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                    @Override
                    public void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup, int i, boolean b) {
                        MaterialButton choice = null;
                        ColorStateList button_colour = null;
                        Snackbar snackbar = null;
                        
                        // sees if a button was clicked (if entrant chose an answer)
                        if (b) {
                            // finds which button entrant clicked (accept or decline)
                            if (i == R.id.accept_button) {
                                snackbar = Snackbar.make(view, "You have accepted your offer.", Snackbar.LENGTH_SHORT);
                                choice = accept_button;
                                button_colour = ContextCompat.getColorStateList(getContext(), R.color.accept_green);

                                    // if person had previously answered, then only keeps the most recently clicked button (accept)
                                decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                                decline_button.setShapeAppearanceModel(original_decline);
                                
                                db.answerEvent(notification.getId(), "Accepted");
                                // saves their answer as "accept"
                                notification.setStatus("Accepted");
                            }
                            // declined button was clicked
                            else if (i == R.id.decline_button) {
                                snackbar = Snackbar.make(view, "You have declined your offer.", Snackbar.LENGTH_SHORT);
                                choice = decline_button;
                                button_colour = ContextCompat.getColorStateList(getContext(), R.color.decline_red);

                                    // if person had previously answered, then only keeps the most recently clicked button (decline)
                                accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                                accept_button.setShapeAppearanceModel(original_accept);
                                
                                db.answerEvent(notification.getId(), "Declined");
                                // saves their answer as "decline"
                                notification.setStatus("Declined");
                            }
                            
                            // makes chosen button into a circle, and colours it
                            ShapeAppearanceModel chosen = choice.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setAllCorners(CornerFamily.ROUNDED, 100f)
                                    .build();
                            choice.setShapeAppearanceModel(chosen);
                            choice.setBackgroundTintList(button_colour);
                        }
                        // if person clicked a button two times in a row
                        else {
                            if (notification.getStatus() == "Accepted") {
                                snackbar = Snackbar.make(view, "You have accepted your offer.", Snackbar.LENGTH_SHORT);
                            } else if (notification.getStatus() == "Declined") {
                                snackbar = Snackbar.make(view, "You have declined your offer.", Snackbar.LENGTH_SHORT);
                            }
                        }
                        
                        // puts snackbar notification higher
                        snackbar.getView().setTranslationY(-220);
                        snackbar.show();
                    }
                });
                
            }
        }
        return view;
    }
}