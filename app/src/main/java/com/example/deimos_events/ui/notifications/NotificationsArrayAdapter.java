package com.example.deimos_events.ui.notifications;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.deimos_events.R;
    import com.example.deimos_events.ui.events.EventTest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;

public class NotificationsArrayAdapter extends ArrayAdapter<EventTest>{
    
    public NotificationsArrayAdapter(Context context, ArrayList<EventTest> events) {
        super(context, 0, events);
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        
        EventTest notification = getItem(position);
        
        
        if (convertView == null) {
            // sees whether user was kept as a waitlist, or chosen by lottery
            if (notification.getWaitingToAccept() != -1) {
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
            imageView.setImageResource(notification.image);
            
            // person is not accepted, shows option to be removed from waiting list
            if (notification.waitingToAccept == -1) {
                MaterialButton button = view.findViewById(R.id.placeholder_button);
                Drawable icon_button = ContextCompat.getDrawable(this.getContext(), R.drawable.cancel_24dp);
                ColorStateList button_colour = ContextCompat.getColorStateList(this.getContext(), com.google.android.material.R.color.design_default_color_error);
                button.setText("Cancel");
                button.setIcon(icon_button);
                button.setBackgroundTintList(button_colour);
            }
            // gives person the choices to accept/decline offer
            else {
                MaterialButtonToggleGroup waitingListAnswer = view.findViewById(R.id.splitButtonLayout);
                MaterialButton accept_button = view.findViewById(R.id.accept_button);
                MaterialButton decline_button = view.findViewById(R.id.decline_button);
                
                waitingListAnswer.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                    @Override
                    public void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup, int i, boolean b) {
                        MaterialButton choice;
                        ColorStateList button_colour;
                        // sees if a button was clicked (if entrant chose an answer)
                        if (b) {
                            // finds which button entrant clicked (accept or decline)
                            if (i == R.id.accept_button) {
                                Toast.makeText(NotificationsArrayAdapter.this.getContext(), "You have accepted your offer.", Toast.LENGTH_SHORT).show();
                                button_colour = ContextCompat.getColorStateList(getContext(), R.color.teal_700);
                                choice = accept_button;
                                
                                // if person had previously answered, then only keeps the most recently clicked button (accept)
                                if (notification.getWaitingToAccept() == 2) {
                                    decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
                                    decline_button.setCornerRadius(10);
                                }
                                
                                // saves their answer as "accept"
                                notification.setWaitingToAccept(1);
                            }
                            // declined button was clicked
                            else {
                                Toast.makeText(NotificationsArrayAdapter.this.getContext(), "You have declined your offer.", Toast.LENGTH_SHORT).show();
                                choice = decline_button;
                                button_colour = ContextCompat.getColorStateList(getContext(), com.google.android.material.R.color.design_default_color_error);
                                
                                // if person had previously answered, then only keeps the most recently clicked button (decline)
                                if (notification.getWaitingToAccept() == 1) {
                                    accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
                                    accept_button.setCornerRadius(10);
                                }
                                
                                // saves their answer as "decline"
                                notification.setWaitingToAccept(2);
                            }
                            choice.setCornerRadius(100);
                            choice.setBackgroundTintList(button_colour);
                        }
                        // if person clicked a button two times in a row (aka deselect)
                        else {
                            String changed_answer = new String();
                            // changes the "accept" button back to default
                            if (notification.getWaitingToAccept() == 1) {
                                accept_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
                                changed_answer = "acceptance";
                                accept_button.setCornerRadius(10);
                            }
                            else if (notification.getWaitingToAccept() == 2) {
                                decline_button.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
                                changed_answer = "rejection";
                                decline_button.setCornerRadius(10);
                            }
                            
                            // saves their answer as "unanswered"
                            notification.setWaitingToAccept(0);
                            Toast.makeText(NotificationsArrayAdapter.this.getContext(), "You have rescinded your " + changed_answer + ".", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
              
            }
        }
        return view;
    }
}
