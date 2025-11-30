package com.example.deimos_events.ui.notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentSendNotificationsBinding;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SendNotificationsFragment extends DialogFragment {
    private FragmentSendNotificationsBinding binding;
    private FlexboxLayout flexboxLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> recipientsOptions = new ArrayList<>(Arrays.asList("Everyone", "Waitlisted", "Pending Answers", "Accepted Offer", "Declined Offer"));
    private ArrayList<String> selectedPersons = new ArrayList<>();
    private SendNotificationsAdapter adapter;
    private TextInputLayout messageLayout;
    private ImageView closeButton;
    private SessionManager SM;
    private Button sendButton;
    
    /**
     * Creates the dialog to send message to people who joined an organizer's events.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        SM = app.getSessionManager();
        // gets data
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();
        UserInterfaceManager UIM = SM.getUserInterfaceManager();
        Event currentEvent = UIM.getCurrentEvent();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        binding = FragmentSendNotificationsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        
        flexboxLayout = binding.flexboxLayout;
        autoCompleteTextView = binding.autoCompleteTags;
        
        // dropdown adapter (aka. suggestions of who to pick)
        adapter = new SendNotificationsAdapter(requireContext(), recipientsOptions);
        autoCompleteTextView.setAdapter(adapter);
        
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                autoCompleteTextView.showDropDown();
            }
        });
        
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());
        
        // clicking people from the given suggestions
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = adapter.getItem(position);
            if (selected != null) {
                // remembers who were selected so as to avoid duplicates
                selectedPersons.add(selected);
                
                Chip chip = new Chip(requireContext(), null,
                        com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
                chip.setText(selected);
                chip.setCloseIconVisible(true);
                
                // chip design based off who they represent
                switch (selected) {
                    case "Declined Offer":
                        chip.setChipBackgroundColorResource(R.color.decline_red);
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                        chip.setCloseIconTintResource(R.color.white);
                        break;
                    
                    case "Accepted Offer":
                        chip.setChipBackgroundColorResource(R.color.accept_green);
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                        chip.setCloseIconTintResource(R.color.white);
                        break;
                    
                    case "Everyone":
                        chip.setChipBackgroundColorResource(R.color.black);
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                        chip.setCloseIconTintResource(R.color.white);
                        break;
                }
                
                // remove selected from the dropdown as to avoid duplicates
                recipientsOptions.remove(selected);
                adapter = new SendNotificationsAdapter(requireContext(), recipientsOptions);
                autoCompleteTextView.setAdapter(adapter);
                
                // margins between chips
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 0, 5, 0);
                chip.setLayoutParams(params);
                
                // to add the chip before the input cursor
                int inputIndex = flexboxLayout.indexOfChild(autoCompleteTextView);
                flexboxLayout.addView(chip, inputIndex);
                
                autoCompleteTextView.setHint("");
                
                // removes selected chips
                chip.setOnCloseIconClickListener(v -> {
                    flexboxLayout.removeView(chip);
                    selectedPersons.remove(selected);
                    
                    recipientsOptions.add(selected); // add back to dropdown
                    adapter = new SendNotificationsAdapter(requireContext(), recipientsOptions);
                    autoCompleteTextView.setAdapter(adapter);
                    
                    // put hint back if there's no selected people
                    if (countChips() == 0) {
                        autoCompleteTextView.setHint("Type a recipient");
                    }
                });
                autoCompleteTextView.setText(""); // clear input
            }
        });
        
        // button to exit the dialog fragment/popup
        closeButton = binding.closeButton;
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        // removes background layout issue
        builder.getContext().setTheme(R.style.Theme_DeimosEvents_Dialog);
        
        // add all people who want to be notified and are part of the event
        db.getNotificationReceivers(currentEvent.getId(), recipientsOptions, receivers -> {
            for (Map<String, String> receiver : receivers) {
                String name = receiver.get("name");
                if (name != null && !recipientsOptions.contains(name)) {
                    recipientsOptions.add(name);
                }
            }
            adapter.notifyDataSetChanged();
        });
        
        // button to send message
        sendButton = binding.sendMessage;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //message layout
                messageLayout = binding.messageLayout;
                EditText editText = messageLayout.getEditText();
                String message = editText.getText().toString();
                
                if (!message.isEmpty()) {
                    // finds who receives notifications
                    db.getNotificationReceivers(currentEvent.getId(), selectedPersons, receivers -> {
                        
                        for (Map<String,String> recipient : receivers) {
                            String deviceId = recipient.get("deviceIdentifier");
                            String registrationId = recipient.get("registrationId");
                            
                            // makes notification
                            db.setNotifications(actor.getDeviceIdentifier(),
                                    deviceId,
                                    message,
                                    currentEvent.getId(),
                                    registrationId);
                        }
                    });
                    
                    //TODO: maybe add a snackbar about sending message?
                    
                    dismiss();
                }
            }
        });
        
        return builder.setView(root).create();
    }
    
    /**
     * Counts chips to determine whether to put hint back or not
     * @return: number of chips
     */
    private int countChips() {
        int count = 0;
        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
            if (flexboxLayout.getChildAt(i) instanceof Chip) count++;
        }
        return count;
    }
    
}