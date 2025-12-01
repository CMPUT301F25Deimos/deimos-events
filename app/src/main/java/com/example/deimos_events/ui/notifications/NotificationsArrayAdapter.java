package com.example.deimos_events.ui.notifications;

import android.annotation.SuppressLint;
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

import com.example.deimos_events.EventsApp;
import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.dataclasses.Notifications;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Array adapter for the notifications
 * - Designs buttons depending on whether user was accepted to the waiting list, or if they accepted/declined their offer
 */
public class NotificationsArrayAdapter extends ArrayAdapter<Notifications>{
    private IDatabase db;
    private Actor actor;
    private SessionManager SM;
    private EventManager EM;

    public NotificationsArrayAdapter(Context context, ArrayList<Notifications> events, IDatabase db, Actor actor) {
        super(context, 0, events);
        this.db = db;
        this.actor = actor;
        EventsApp app = (EventsApp) context.getApplicationContext();
        SM = app.getSessionManager();
        EM = SM.getEventManager();
    }
    
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        Notifications notification = getItem(position);
        
        String status = notification.getStatus();

        if (status == null) {
            status = "Waiting";
        }

        if (convertView == null) {
            // sees whether user was kept as a waitlist, or chosen by lottery
            if (status.equals("Waiting")) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_no_button, parent, false);
            }
            else if (!status.equals("Waitlisted") && !status.equals("Rejected Waitlist")) {
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
            if (status.equals("Waiting")) {
                // sets notification message for messages which organizers just want to send
                textView.setText(notification.message);
            }
            // person is not accepted, shows option to be removed from waiting list
            else if (status.equals("Waitlisted") || status.equals("Rejected Waitlist") ) {
                // sets notification message for if user was not accepted in the event lottery
                textView.setText(notification.message);
                MaterialButton button = view.findViewById(R.id.placeholder_button);
                Drawable icon_button;
                ColorStateList button_colour;
                String button_text;

                if (status.equals("Waitlisted")) {
                    icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.cancel_24dp);
                    button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.decline_red);
                    button_text = "Cancel";

                }
                else {
                    icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.join_sticker_24dp);
                    button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.title_colour);
                    button_text = "Join";
                }

                button.setText(button_text);
                button.setIcon(icon_button);
                button.setBackgroundTintList(button_colour);

                button.setOnClickListener(v -> {
                    Drawable changed_icon_button;
                    ColorStateList changed_button_colour;
                    String changed_button_text;

                    Snackbar snackbar;
                    if (button.getText().equals("Cancel")) {
                        // (changed it from leaveEvent so that it's still part of user's joined events history)
                        EM.answerEvent(notification.getId(), "Rejected Waitlist", result ->{
                            if (!result.isSuccess()){
                                Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                        EM.setRegistrationStatus(notification.getRegistrationId(), "Rejected Waitlist", result ->{
                            if (!result.isSuccess()){
                                Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });


                        // OPtion to join the event once again after leaving
                        changed_icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.join_sticker_24dp);
                        changed_button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.title_colour);
                        changed_button_text = "Join";

                        snackbar = Snackbar.make(view, "You have cancelled the offer to be part of the waitlist for the event, '" + notification.title + "'.", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
                    }
                    else {
                        // join waiting list once again
                        EM.answerEvent(notification.getId(), "Waitlisted", result ->{
                            if (!result.isSuccess()){
                                Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                        EM.setRegistrationStatus(notification.getRegistrationId(), "Waitlisted", result ->{
                            if (!result.isSuccess()){
                                Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });


                        // Reverts it back to its prevoius state
                        changed_icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.cancel_24dp);
                        changed_button_colour = ContextCompat.getColorStateList(this.getContext(), R.color.decline_red);
                        changed_button_text = "Cancel";

                        snackbar = Snackbar.make(view, "You have once again joined the second waitlist for the event, '" + notification.title + "'.", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
                    }

                    button.setText(changed_button_text);
                    button.setIcon(changed_icon_button);
                    button.setBackgroundTintList(changed_button_colour);
                    snackbar.getView().setTranslationY(-260);
                    snackbar.show();
                });
            }
            // gives person the choices to accept/decline offer
            else {
                // sets notification message for if user was not accepted in the event lottery
                textView.setText(notification.message);
                MaterialButtonToggleGroup waitingListAnswer = view.findViewById(R.id.split_button_layout);
                MaterialButton accept_button = view.findViewById(R.id.accept_button);
                MaterialButton decline_button = view.findViewById(R.id.decline_button);
                
                // changes accept/decline button roundness so that they appear as they originally looked like
                ShapeAppearanceModel original_accept = accept_button.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, 10f)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 10f)
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

                // chosen button shape
                ShapeAppearanceModel circle_button = ShapeAppearanceModel.builder()
                        .setAllCorners(CornerFamily.ROUNDED, 100f)
                        .build();

                
                if (status.equals("Accepted")) {
                    // changes selected button to circle and to its proper colour
                    accept_button.post(() -> {
                        accept_button.setShapeAppearanceModel(circle_button);
                    });
                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.accept_green));
                    
                    // changes decline button to its unclicked state
                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                    decline_button.setShapeAppearanceModel(original_decline);
                    
                } else if (status.equals("Declined")) {
                    // changes selected button to circle and to its proper colour
                    decline_button.post(() -> {
                        decline_button.setShapeAppearanceModel(circle_button);
                    });
                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.decline_red));
                    
                    // changes accept button to its unclicked state
                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                    accept_button.setShapeAppearanceModel(original_accept);
                } else {
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
                            if (i == R.id.accept_button) { // finds which button entrant clicked (accept or decline)
                                snackbar = Snackbar.make(view, "You have accepted your offer.", Snackbar.LENGTH_SHORT);
                                choice = accept_button;
                                button_colour = ContextCompat.getColorStateList(getContext(), R.color.accept_green);

                                // if person had previously answered, then only keeps the most recently clicked button (accept)
                                decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                                decline_button.setShapeAppearanceModel(original_decline);
                                
                                EM.answerEvent(notification.getId(), "Accepted", result ->{
                                    if (!result.isSuccess()){
                                        Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                                // saves their answer as "accept"
                                EM.setRegistrationStatus(notification.getRegistrationId(), "Accepted", result ->{
                                    if (!result.isSuccess()){
                                        Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            } else if (i == R.id.decline_button) { // declined button was clicked
                                snackbar = Snackbar.make(view, "You have declined your offer.", Snackbar.LENGTH_SHORT);
                                choice = decline_button;
                                button_colour = ContextCompat.getColorStateList(getContext(), R.color.decline_red);

                                    // if person had previously answered, then only keeps the most recently clicked button (decline)
                                accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.title_colour));
                                accept_button.setShapeAppearanceModel(original_accept);
                                
                                EM.answerEvent(notification.getId(), "Cancelled",  result ->{
                                    if (!result.isSuccess()){
                                        Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });

                                // saves their answer as "decline"
                                EM.setRegistrationStatus(notification.getRegistrationId(), "Declined", result ->{
                                    if (!result.isSuccess()){
                                        Snackbar.make(view, result.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                            
                            // makes chosen button into a circle, and colours it
                            choice.setShapeAppearanceModel(circle_button);
                            choice.setBackgroundTintList(button_colour);
                        } else { // if person clicked a button more than once in a row
                            if (notification.status.equals("Accepted")) {
                                snackbar = Snackbar.make(view, "You have accepted your offer.", Snackbar.LENGTH_SHORT);
                            } else if (notification.status.equals("Declined")) {
                                snackbar = Snackbar.make(view, "You have declined your offer.", Snackbar.LENGTH_SHORT);
                            }
                        }
                        
                        // puts snackbar notification higher
                        if (snackbar != null) {
                            snackbar.getView().setTranslationY(-260);
                            snackbar.show();
                        }
                    }
                });
                
            }
        }
        return view;
    }

}