package com.example.deimos_events.ui.picker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.dataclasses.Registration;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.List;
/**
 * Adapter used to display a list of registrations for a given event.
 * <p>
 * Each row shows:
 * <ul>
 *     <li>Entrant name</li>
 *     <li>The entrant's registration status</li>
 *     <li>An optional delete button (only visible for "Pending" registrations)</li>
 * </ul>
 * <p>
 * When the delete button is pressed (for Pending registrations), the registration
 * is removed from the database, removed from the adapter list, and a callback is
 * triggered to allow the parent fragment to refresh related UI.
 */
public class RegistrationAdapter extends ArrayAdapter<Registration> {
    /**
     * Listener interface used to notify the parent when a registration is deleted.
     */

    public interface OnDeleteListener {
        /**
         * Called when a registration is deleted.
         *
         * @param status The status of the deleted registration.
         */
        void onDelete(String status);
    }
    private OnDeleteListener listener;
    /**
     * Constructs the registration adapter.
     *
     * @param context       The current context.
     * @param registrations The list of registrations to display.
     * @param listener      Optional listener used to notify when a registration is deleted.
     */
    public RegistrationAdapter(@NonNull Context context, @NonNull List<Registration> registrations, @Nullable OnDeleteListener listener) {
        super(context, 0, registrations);
        this.listener = listener;
    }
    /**
     * Inflates and binds the view for each registration row.
     * <p>
     * Displays the entrant name and registration status.
     * Shows a delete (X) button only if the status is "Pending".
     * <p>
     * When delete is pressed:
     * <ul>
     *     <li>The registration is removed from Firestore</li>
     *     <li>The row is removed from the list</li>
     *     <li>The adapter refreshes</li>
     *     <li>The OnDeleteListener callback is invoked (if provided)</li>
     * </ul>
     *
     * @param position    The index of the item within the list.
     * @param convertView A recycled view to reuse (if available).
     * @param parent      The parent view group.
     * @return The fully bound row view.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.entrant_view_and_x, parent, false);
        }

        SessionManager SM = ((EventsApp) getContext().getApplicationContext()).getSessionManager();
        EventManager EM = SM.getEventManager();
        Event event = SM.getSession().getCurrentEvent();

        Registration registration = getItem(position);
        if (registration != null) {
            TextView nameView = convertView.findViewById(R.id.name);
            TextView statusView = convertView.findViewById(R.id.status);
            ImageButton deleteButton = convertView.findViewById(R.id.x);

            EM.getActorById(registration.getEntrantId(), actor -> {
                nameView.setText(actor.getName());
                statusView.setText(registration.getStatus());

                if ("Pending".equals(registration.getStatus())) {
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(v -> {
                        EM.deleteRegistration(registration.getId(), callback -> {
                            Toast.makeText(getContext(), "Registration Deleted", Toast.LENGTH_SHORT).show();

                            remove(registration);
                            notifyDataSetChanged();

                            if (listener != null) listener.onDelete(registration.getStatus());
                        });
                    });
                } else {
                    deleteButton.setVisibility(View.GONE);
                }
            });
        }

        return convertView;
    }
}
