package com.example.deimos_events.ui.events;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.Actor;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Registration;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentEntrantsEventsBinding;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment responsible for displaying events to entrants.
 * <p>
 * Supports:
 * <ul>
 *     <li>Filtering by status (all, joined, waitlisted, selected, not selected)</li>
 *     <li>Filtering by availability (weekdays, weekends)</li>
 *     <li>Filtering by interests using chips</li>
 *     <li>"My history" view, which shows status and hides join/cancel buttons</li>
 * </ul>
 */
public class EventsEntrantsFragment extends Fragment {

    private FragmentEntrantsEventsBinding binding;
    private EventArrayAdapter adapter;
    private ListenerRegistration registrationListener;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private ListView listView;

    /**
     * Indicates if the fragment is currently displaying history mode
     * (no join/cancel button, only status text).
     */
    private boolean historyMode = false;

    /**
     * Live list of all events retrieved from the database.
     */
    private final List<com.example.deimos_events.Event> allEventsLive = new ArrayList<>();

    /**
     * Set of event IDs that the entrant is currently joined to.
     */
    private Set<String> joinedEventIdsLive = new HashSet<>();

    /**
     * Map of event to "day type" (e.g., "Weekdays" or "Weekends") for filtering.
     */
    private final Map<com.example.deimos_events.Event, String> dayTypeByEvent = new HashMap<>();

    /**
     * Map of event to category (e.g., "Swimming", "Sports") for interest filtering.
     */
    private final Map<com.example.deimos_events.Event, String> categoryByEvent = new HashMap<>();

    /**
     * Map of event to timestamp (milliseconds) for ordering in history view.
     */
    private final Map<com.example.deimos_events.Event, Long> timeByEvent = new HashMap<>();

    /**
     * Map of eventId to registration status string for the current entrant
     * (e.g., "Accepted", "Declined", "Pending", "Waitlisted").
     */
    private final Map<String, String> registrationStatusByEventId = new HashMap<>();

    /**
     * Filterable status options for the event list.
     */
    private enum Status { ALL, JOINED, WAITLISTED, SELECTED, NOT_SELECTED }

    /**
     * The currently selected status filter.
     */
    private Status currentStatus = Status.ALL;

    /**
     * Currently selected availability filters (e.g., "Weekdays", "Weekends").
     */
    private final Set<String> selectedDayTypes = new HashSet<>();

    /**
     * Currently selected interest categories for filtering.
     */
    private final Set<String> selectedCategories = new HashSet<>();

    /**
     * View used as the empty view for the ListView when no events are available
     * or nothing matches the filters.
     */
    private TextView emptyView;

    /**
     * Initializes the fragment and acquires references to shared managers.
     *
     * @param savedInstanceState saved state bundle, if any
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EventsApp app = (EventsApp) requireActivity().getApplicationContext();
            SM = app.getSessionManager();
            if (SM != null) {
                UIM = SM.getUserInterfaceManager();
            }
        } catch (Throwable ignored) {}
    }

    /**
     * Inflates the layout, configures UI elements, connects filters, and loads events
     * and registration data for the current entrant.
     *
     * @param inflater           layout inflater
     * @param container          parent container
     * @param savedInstanceState saved state bundle, if any
     * @return root view for this fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEntrantsEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final FloatingActionButton fab = binding.filterEvent;
        fab.setOnClickListener(v -> {
            clearAllFilters();
            Session sess = (SM != null) ? SM.getSession() : null;
            Actor current = (sess != null) ? sess.getCurrentActor() : null;
            historyMode = false;
            renderWithFilters(listView, current);
            Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
        });

        listView = binding.eventsList;

        attachEmptyViewToList("You haven’t joined any events yet.");
        listView.setEmptyView(emptyView);

        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        SM = app.getSessionManager();
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        MaterialButton historyBtn = root.findViewById(R.id.btn_history);
        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                historyMode = true;
                binding.toggleStatus.check(R.id.toggle_status_joined);
                currentStatus = Status.JOINED;
                renderWithFilters(listView, actor);
                Toast.makeText(getContext(),
                        "Showing your event history", Toast.LENGTH_SHORT).show();
            });
        }

        db.getEvents(events -> {
            allEventsLive.clear();
            if (events != null) allEventsLive.addAll(events);
            assignSidecarTagsForRealEvents(allEventsLive);

            db.getEntrantRegisteredEvents(actor, joinedEventIds -> {
                joinedEventIdsLive = (joinedEventIds == null)
                        ? new HashSet<>()
                        : new HashSet<>(joinedEventIds);

                registrationStatusByEventId.clear();
                db.getNotificationEventInfo(actor, registrations -> {
                    for (Registration r : registrations) {
                        if (r.getEventId() != null && r.getStatus() != null) {
                            registrationStatusByEventId.put(r.getEventId(), r.getStatus());
                        }
                    }
                    renderWithFilters(listView, actor);
                });

                registrationListener = db.listenToRegisteredEvents(actor, (updatedJoinedIds) -> {
                    joinedEventIdsLive = (updatedJoinedIds == null)
                            ? new HashSet<>()
                            : new HashSet<>(updatedJoinedIds);

                    registrationStatusByEventId.clear();
                    db.getNotificationEventInfo(actor, registrations -> {
                        for (Registration r : registrations) {
                            if (r.getEventId() != null && r.getStatus() != null) {
                                registrationStatusByEventId.put(r.getEventId(), r.getStatus());
                            }
                        }
                        renderWithFilters(listView, actor);
                    });
                });
            });
        });

        binding.toggleStatus.check(R.id.toggle_status_all);
        binding.toggleStatus.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            if (checkedId == R.id.toggle_status_all) {
                currentStatus = Status.ALL;
            } else if (checkedId == R.id.toggle_status_joined) {
                currentStatus = Status.JOINED;
            } else if (checkedId == R.id.toggle_status_waitlisted) {
                currentStatus = Status.WAITLISTED;
            } else if (checkedId == R.id.toggle_status_selected) {
                currentStatus = Status.SELECTED;
            } else if (checkedId == R.id.toggle_status_not_selected) {
                currentStatus = Status.NOT_SELECTED;
            }
            renderWithFilters(listView, actor);
        });

        binding.toggleAvailability.addOnButtonCheckedListener((group, id, isChecked) -> {
            if (id == R.id.toggle_avail_weekdays) {
                if (isChecked) selectedDayTypes.add("Weekdays");
                else selectedDayTypes.remove("Weekdays");
            } else if (id == R.id.toggle_avail_weekends) {
                if (isChecked) selectedDayTypes.add("Weekends");
                else selectedDayTypes.remove("Weekends");
            }
            renderWithFilters(listView, actor);
        });

        ChipGroup chips = binding.chipsInterests;
        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedCategories.clear();
            for (Integer cid : checkedIds) {
                Chip c = group.findViewById(cid);
                if (c != null) selectedCategories.add(c.getText().toString());
            }
            renderWithFilters(listView, actor);
        });

        return root;
    }

    /**
     * Clears all active filters (status, availability, interests) and resets the UI controls
     * to their default state.
     */
    private void clearAllFilters() {
        binding.toggleStatus.check(R.id.toggle_status_all);
        currentStatus = Status.ALL;

        for (int i = 0; i < binding.toggleAvailability.getChildCount(); i++) {
            View child = binding.toggleAvailability.getChildAt(i);
            binding.toggleAvailability.uncheck(child.getId());
        }
        selectedDayTypes.clear();

        binding.chipsInterests.clearCheck();
        selectedCategories.clear();
    }

    /**
     * Assigns synthetic "sidecar" tags to real events for day type, category,
     * and timestamp so that filtering and sorting can be demonstrated or applied.
     *
     * @param events list of events retrieved from the database
     */
    private void assignSidecarTagsForRealEvents(List<com.example.deimos_events.Event> events) {
        dayTypeByEvent.clear();
        categoryByEvent.clear();
        timeByEvent.clear();

        Calendar base = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            com.example.deimos_events.Event e = events.get(i);

            String day = (i % 2 == 0) ? "Weekdays" : "Weekends";
            dayTypeByEvent.put(e, day);

            String cat;
            switch (i % 5) {
                case 0: cat = "Swimming"; break;
                case 1: cat = "Sports";   break;
                case 2: cat = "Dance";    break;
                case 3: cat = "Games";    break;
                default: cat = "Music";   break;
            }
            categoryByEvent.put(e, cat);

            Calendar c = (Calendar) base.clone();
            c.add(Calendar.DAY_OF_YEAR, -i);
            c.set(Calendar.HOUR_OF_DAY, 10);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            timeByEvent.put(e, c.getTimeInMillis());
        }
    }

    /**
     * Applies all current filters (status, availability, category) to the live event list,
     * creates a new {@link EventArrayAdapter}, and updates the ListView.
     *
     * @param listView the ListView displaying events
     * @param actor    the current entrant actor
     */
    private void renderWithFilters(ListView listView, Actor actor) {
        List<com.example.deimos_events.Event> filtered = new ArrayList<>();
        for (com.example.deimos_events.Event e : allEventsLive) {
            if (!matchesStatus(e)) continue;
            if (!matchesAvailability(e)) continue;
            if (!matchesCategory(e)) continue;
            filtered.add(e);
        }

        if (currentStatus == Status.JOINED) {
            Collections.sort(filtered, (a, b) -> {
                long ta = timeByEvent.containsKey(a) ? timeByEvent.get(a) : 0L;
                long tb = timeByEvent.containsKey(b) ? timeByEvent.get(b) : 0L;
                return Long.compare(tb, ta);
            });
        }

        NavController navController = NavHostFragment.findNavController(this);

        adapter = new EventArrayAdapter(
                requireContext(),
                filtered,
                joinedEventIdsLive,
                registrationStatusByEventId,
                historyMode,
                SM,
                actor,
                navController
        );
        listView.setAdapter(adapter);

        if (emptyView != null) {
            if (currentStatus == Status.JOINED) {
                emptyView.setText("You haven’t joined any events yet.");
            } else {
                emptyView.setText("No events match your filters.");
            }
        }
    }

    /**
     * Checks whether the given event matches the currently selected status filter.
     *
     * @param e the event to test
     * @return true if the event passes the status filter, false otherwise
     */
    private boolean matchesStatus(com.example.deimos_events.Event e) {
        String status = registrationStatusByEventId.get(e.getId());
        String norm = (status == null) ? "" : status.toUpperCase();

        boolean joined = joinedEventIdsLive.contains(e.getId());

        switch (currentStatus) {
            case ALL:
                return true;

            case JOINED:
                return joined;

            case WAITLISTED:
                return "WAITLISTED".equals(norm);

            case SELECTED:
                return "ACCEPTED".equals(norm) || "SELECTED".equals(norm);

            case NOT_SELECTED:
                return "DECLINED".equals(norm)
                        || "REJECTED".equals(norm)
                        || "NOT_SELECTED".equals(norm);

            default:
                return true;
        }
    }

    /**
     * Checks whether the given event matches the current availability filter
     * (e.g., weekdays/weekends).
     *
     * @param e the event to test
     * @return true if the event passes the availability filter, false otherwise
     */
    private boolean matchesAvailability(com.example.deimos_events.Event e) {
        if (selectedDayTypes.isEmpty()) return true;
        String day = dayTypeByEvent.get(e);
        return day != null && selectedDayTypes.contains(day);
    }

    /**
     * Checks whether the given event matches the currently selected interest categories.
     *
     * @param e the event to test
     * @return true if the event passes the category filter, false otherwise
     */
    private boolean matchesCategory(com.example.deimos_events.Event e) {
        if (selectedCategories.isEmpty()) return true;
        String cat = categoryByEvent.get(e);
        return cat != null && selectedCategories.contains(cat);
    }

    /**
     * Creates and attaches an empty view to the root layout and configures its
     * position and appearance to be used as the ListView's empty view.
     *
     * @param initialText initial text to show when the list is empty
     */
    private void attachEmptyViewToList(String initialText) {
        if (!(binding.getRoot() instanceof ConstraintLayout)) return;
        ConstraintLayout root = (ConstraintLayout) binding.getRoot();

        emptyView = new TextView(requireContext());
        emptyView.setId(View.generateViewId());
        emptyView.setText(initialText);
        emptyView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        emptyView.setTypeface(Typeface.DEFAULT_BOLD);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(16f);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        emptyView.setPadding(pad, pad, pad, pad);

        root.addView(emptyView, new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ConstraintSet cs = new ConstraintSet();
        cs.clone(root);
        cs.connect(emptyView.getId(), ConstraintSet.TOP,    R.id.filters_quick_bar, ConstraintSet.BOTTOM, pad);
        cs.connect(emptyView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, pad);
        cs.connect(emptyView.getId(), ConstraintSet.START,  ConstraintSet.PARENT_ID, ConstraintSet.START, pad);
        cs.connect(emptyView.getId(), ConstraintSet.END,    ConstraintSet.PARENT_ID, ConstraintSet.END, pad);
        cs.applyTo(root);
    }

    /**
     * Cleans up resources when the view is destroyed, including removing
     * any active Firestore listener registrations.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registrationListener != null) {
            registrationListener.remove();
            registrationListener = null;
        }
        binding = null;
    }
}
