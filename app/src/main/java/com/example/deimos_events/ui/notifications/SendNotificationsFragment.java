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

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentSendNotificationsBinding;
import com.example.deimos_events.managers.NotificationManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * A dialog fragment that allows organizers to send notifications to selected
 * recipients of an event. Recipients can be chosen by status (e.g., Accepted,
 * Waitlisted, Declined) or by individual name. Multiple selections are displayed
 * as removable chips, and messages are sent via the {@link NotificationManager}.
 */

public class SendNotificationsFragment extends DialogFragment {
    private FragmentSendNotificationsBinding binding;
    private FlexboxLayout flexboxLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private List<String> statuses = Arrays.asList("Everyone", "Waiting", "Waitlisted", "Pending", "Accepted", "Declined", "Rejected Waitlist");
    private ArrayList<String> recipientsOptions = new ArrayList<>(statuses);
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
        NotificationManager NM = SM.getNotificationManager();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        binding = FragmentSendNotificationsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        
        flexboxLayout = binding.flexboxLayout;
        autoCompleteTextView = binding.autoCompleteTags;
        
        // dropdown adapter (aka. suggestions of who to pick)
        adapter = new SendNotificationsAdapter(requireContext(), recipientsOptions);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(0); // show suggestions/dropdown after typing 0 letters
        
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
                if (selected.equals("Declined") || selected.equals("Rejected Waitlist")) {
                    chip.setChipBackgroundColorResource(R.color.decline_red);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    chip.setCloseIconTintResource(R.color.white);
                }
                else if (selected.equals("Accepted")) {
                    chip.setChipBackgroundColorResource(R.color.accept_green);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    chip.setCloseIconTintResource(R.color.white);
                }
                else if (selected.equals("Waiting") || selected.equals("Pending") || selected.equals("Waitlisted")) {
                    chip.setChipBackgroundColorResource(R.color.title_colour);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    chip.setCloseIconTintResource(R.color.white);
                }
                else if (selected.equals("Everyone")) {
                    chip.setChipBackgroundColorResource(R.color.black);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    chip.setCloseIconTintResource(R.color.white);
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
                    
                    // sorts recipientsOptions (aka dropdown) so that the first options are the given statuses (eg. "Everyone", "Waitlisted", etc)
                    // whereas the names after that are sorted alphabetically
                    recipientsOptions.sort((a, b) -> {
                        int elementA = statuses.indexOf(a);
                        int elementB = statuses.indexOf(b);
                        
                        // if both are in statuses
                        if (elementA != -1 && elementB != -1) {
                            // finds the elements' indexes in status (AKA. which comes first)
                            return Integer.compare(elementA, elementB);
                        }
                        else if (elementA != -1) {
                            return -1;
                        }
                        else if (elementB != -1) {
                            return 1;
                        }
                        else {
                            // alphabetical sort if not in statuses
                            return a.compareTo(b);
                        }
                    });
                    
                    adapter = new SendNotificationsAdapter(requireContext(), recipientsOptions);
                    autoCompleteTextView.setAdapter(adapter);
                    
                    // put hint back if there's no selected people
                    if (countChips() == 0) {
                        autoCompleteTextView.setHint("Type a recipient...");
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
        NM.fetchNotificationReceivers(currentEvent.getId(), recipientsOptions, receivers -> {
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
                
                if (!message.isEmpty() && countChips() > 0) {
                    // finds who receives notifications
                    NM.fetchNotificationReceivers(currentEvent.getId(), selectedPersons, receivers -> {
                        for (Map<String,String> recipient : receivers) {
                            String deviceId = recipient.get("deviceIdentifier");
                            String registrationId = recipient.get("registrationId");
                            
                            // makes notification
                            NM.insertNotifications(actor.getDeviceIdentifier(),
                                    deviceId,
                                    message,
                                    currentEvent.getId(),
                                    registrationId,
                                    result ->{
                                        if (!result.isSuccess() && isAdded()){
                                            Snackbar.make(v, result.getMessage(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    
                    Snackbar snackbar;
                    snackbar = Snackbar.make(getParentFragment().getView(), "You have successfully sent a message!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT);
                    snackbar.getView().setTranslationY(-260);
                    snackbar.show();
                    
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