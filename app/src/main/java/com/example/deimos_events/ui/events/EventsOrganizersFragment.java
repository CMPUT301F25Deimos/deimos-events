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
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentOrganizersEventsBinding;
import com.example.deimos_events.managers.EventManager;
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
 * Fragment that displays and manages the organizer's event list with rich filtering options.
 * <p>Features include:</p>
 * <ul>
 *     <li>Viewing all events relevant to the current organizer or entrant</li>
 *     <li>Filtering by join status (all, joined, not joined, etc.)</li>
 *     <li>Filtering by availability (weekdays / weekends)</li>
 *     <li>Filtering by interest categories using chips</li>
 *     <li>Navigating to create-event and edit-event screens</li>
 *     <li>Listening to live updates of joined events via Firestore</li>
 * </ul>
 */
public class EventsOrganizersFragment extends Fragment {
    private FragmentOrganizersEventsBinding binding;
    private EventArrayAdapter adapter;
    private ListenerRegistration registrationListener;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private ListView listView;
    private final List<Event> allEventsLive = new ArrayList<>();
    private Set<String> joinedEventIdsLive = new HashSet<>();

    private final Map<Event, String> dayTypeByEvent = new HashMap<>();
    private final Map<Event, String> categoryByEvent = new HashMap<>();
    private final Map<Event, Long> timeByEvent = new HashMap<>();

    /**
     * Filterable status options for the event list.
     */
    private enum Status { ALL, JOINED, WAITLISTED, SELECTED, NOT_SELECTED }
    private Status currentStatus = Status.ALL;
    private final Set<String> selectedDayTypes = new HashSet<>();
    private final Set<String> selectedCategories = new HashSet<>();

    private TextView emptyView;

    /**
     * Initializes managers from the application context.
     *
     * @param savedInstanceState previously saved state, if any
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
     * Inflates the view, wires up filter UI, loads events and registration state, and sets up live listeners.
     *
     * @param inflater  layout inflater
     * @param container parent container
     * @param savedInstanceState previously saved state, if any
     * @return the root view for this fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrganizersEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final FloatingActionButton fab = binding.filterEvent;
        fab.setOnClickListener(v -> {
            clearAllFilters();
            ListView lv = binding.eventsList;
            Session sess = (SM != null) ? SM.getSession() : null;
            Actor current = (sess != null) ? sess.getCurrentActor() : null;
            renderWithFilters(lv, current);
            Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
        });

        // gets data
        listView = binding.eventsList;

        attachEmptyViewToList("You haven’t joined any events yet.");
        listView.setEmptyView(emptyView);

        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        SM = app.getSessionManager();
        EventManager EM = SM.getEventManager();
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();

        EM.fetchEvents(events -> {
            allEventsLive.clear();
            allEventsLive.addAll(events);
            assignSidecarTagsForRealEvents(allEventsLive);

            EM.fetchEntrantRegisteredEvents(actor, joinedEventIds -> {
                joinedEventIdsLive = new HashSet<>(joinedEventIds);
                renderWithFilters(listView, actor);

                registrationListener = EM.listenToRegisteredEvents(actor, (updatedJoinedIds) -> {
                    joinedEventIdsLive = new HashSet<>(updatedJoinedIds);
                    renderWithFilters(listView, actor);
                });
            });
        });

        binding.addEvent.setOnClickListener(view ->
                NavHostFragment.findNavController(EventsOrganizersFragment.this)
                        .navigate(R.id.navigation_create)
        );

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

        MaterialButton historyBtn = root.findViewById(R.id.btn_history);
        if (historyBtn != null) {
            historyBtn.setOnClickListener(v -> {
                binding.toggleStatus.check(R.id.toggle_status_joined);
                currentStatus = Status.JOINED;
                renderWithFilters(listView, actor);
                Toast.makeText(getContext(), "Showing joined events", Toast.LENGTH_SHORT).show();
            });
        }

        return root;
    }

    /**
     * Navigate to edit screen for an event (unchanged logic).
     */
    public void edit(Event event) {
        NavController navController = NavHostFragment.findNavController(this);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_organizers_events, true)
                .build();
        Bundle arg = new Bundle();
        arg.putString("id", event.getId());
        navController.navigate(R.id.navigation_edit, arg, navOptions);
    }

    /**
     * Resets all filter controls and in-memory selections to their default state.
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
     * Populates sidecar maps used for filtering and sorting without altering the Event model.
     *
     * @param events source events to tag
     */
    private void assignSidecarTagsForRealEvents(List<Event> events) {
        dayTypeByEvent.clear();
        categoryByEvent.clear();
        timeByEvent.clear();

        Calendar base = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);

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
     * Applies current filters to the live list, sorts if needed, and rebuilds the adapter.
     *
     * @param listView target list view to update
     * @param actor    current actor for contextual filtering
     */
    private void renderWithFilters(ListView listView, Actor actor) {
        List<Event> filtered = new ArrayList<>();
        for (Event e : allEventsLive) {
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
     * Checks whether an event matches the currently selected status filter.
     */
    private boolean matchesStatus(Event e) {
        if (currentStatus == Status.ALL) return true;

        boolean joined = joinedEventIdsLive.contains(e.getId());
        switch (currentStatus) {
            case JOINED:
                return joined;
            case NOT_SELECTED:
                return !joined;
            case WAITLISTED:
            case SELECTED:
                return false;
            default:
                return true;
        }
    }

    /**
     * Checks whether an event matches the currently selected availability constraints.
     */
    private boolean matchesAvailability(Event e) {
        if (selectedDayTypes.isEmpty()) return true;
        String day = dayTypeByEvent.get(e);
        return day != null && selectedDayTypes.contains(day);
    }

    /**
     * Checks whether an event matches the currently selected category constraints.
     */
    private boolean matchesCategory(Event e) {
        if (selectedCategories.isEmpty()) return true;
        String cat = categoryByEvent.get(e);
        return cat != null && selectedCategories.contains(cat);
    }

    /**
     * Creates and attaches a centered TextView as the empty view for the list.
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
     * Cleans up listeners and view bindings when the fragment view is destroyed.
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
