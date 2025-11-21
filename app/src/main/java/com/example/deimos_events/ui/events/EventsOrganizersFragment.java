package com.example.deimos_events.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.Actor;
import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.example.deimos_events.databinding.FragmentOrganizersEventsBinding;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Set;

public class EventsOrganizersFragment extends Fragment {
    
    private FragmentOrganizersEventsBinding binding;
    private EventArrayAdapter adapter;
    private ListenerRegistration registrationListener;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    
    private ListView listView;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentOrganizersEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // gets data
        ListView listView = binding.eventsList;
        
        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        SM = app.getSessionManager();
        Session session = SM.getSession();
        IDatabase db = session.getDatabase();
        Actor actor = session.getCurrentActor();


        db.getEvents(events -> {
            db.getEntrantRegisteredEvents(actor, joinedEventIds -> {
                if (!isAdded()) return;
                adapter = new EventArrayAdapter(requireContext(), events, joinedEventIds, SM, actor);
                listView.setAdapter(adapter);
                registrationListener = db.listenToRegisteredEvents(actor, (updatedJoinedIds) -> {

                });
            });
        });
        
        // clicking fab goes to create event fragment
        binding.addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EventsOrganizersFragment.this).navigate(R.id.navigation_create);
                
            }
        });
        
        return root;
    }
    public void edit(Event event){
        NavController navController = NavHostFragment.findNavController(this);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_organizers_events, true)
                .build();
        Bundle arg = new Bundle();
        arg.putString("id", event.getId());
        navController.navigate(R.id.navigation_edit, arg, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        registrationListener.remove();
    }
}



















// PREVIOUS CODE FROM BEFORE I PULLED IS BELOW: !!!!! (i didn't delete anything)
// was having trouble with it in, but will see if i can try to incorporate it again later

///**
// * Fragment displaying live event data with client-side filtering for status, availability, and category.
// * <p>
// * Data is loaded via {@link SessionManager} and {@link IDatabase}, and a {@link ListView}
// * is rebuilt whenever the underlying data or filter state changes. The FloatingActionButton
// * acts as a "clear all filters" control.
// */
//public class EventsFragment extends Fragment {
//
//    private FragmentEventsBinding binding;
//
//    private SessionManager SM;
//    private UserInterfaceManager UIM;
//
//    private final List<Event> allEventsLive = new ArrayList<>();
//    private Set<String> joinedEventIdsLive = new HashSet<>();
//
//    private final Map<Event, String> dayTypeByEvent = new HashMap<>();
//    private final Map<Event, String> categoryByEvent = new HashMap<>();
//    private final Map<Event, Long>   timeByEvent = new HashMap<>();
//
//    /**
//     * Filterable status options for the event list.
//     */
//    private enum Status { ALL, JOINED, WAITLISTED, SELECTED, NOT_SELECTED }
//    private Status currentStatus = Status.ALL;
//    private final Set<String> selectedDayTypes = new HashSet<>();
//    private final Set<String> selectedCategories = new HashSet<>();
//
//    private TextView emptyView;
//    private ListenerRegistration registrationListener;
//
//    /**
//     * Initializes managers from the application context.
//     *
//     * @param savedInstanceState previously saved state, if any
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            EventsApp app = (EventsApp) requireActivity().getApplicationContext();
//            SM = app.getSessionManager();
//            if (SM != null) {
//                UIM = SM.getUserInterfaceManager();
//            }
//        } catch (Throwable ignored) {}
//    }
//
//    /**
//     * Inflates the view, wires up filter UI, loads events and registration state, and sets up live listeners.
//     *
//     * @param inflater  layout inflater
//     * @param container parent container
//     * @param savedInstanceState previously saved state, if any
//     * @return the root view for this fragment
//     */
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        new ViewModelProvider(this).get(EventsViewModel.class);
//
//        binding = FragmentEventsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final FloatingActionButton fab = binding.filterFab;
//        fab.setOnClickListener(v -> {
//            clearAllFilters();
//            ListView lv = binding.eventsList;
//            Session sess = (SM != null) ? SM.getSession() : null;
//            Actor current = (sess != null) ? sess.getCurrentActor() : null;
//            renderWithFilters(lv, current);
//            Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
//        });
//
//        ListView listView = binding.eventsList;
//        attachEmptyViewToList("You haven’t joined any events yet.");
//        listView.setEmptyView(emptyView);
//
//        Session session = SM.getSession();
//        IDatabase db = session.getDatabase();
//        Actor actor = session.getCurrentActor();
//
//        db.getEvents(events -> {
//            allEventsLive.clear();
//            if (events != null) allEventsLive.addAll(events);
//            assignSidecarTagsForRealEvents(allEventsLive);
//
//            db.getEntrantRegisteredEvents(actor, joinedEventIds -> {
//                joinedEventIdsLive = (joinedEventIds == null) ? new HashSet<>() : new HashSet<>(joinedEventIds);
//                renderWithFilters(listView, actor);
//
//                registrationListener = db.listenToRegisteredEvents(actor, updatedIds -> {
//                    joinedEventIdsLive = (updatedIds == null) ? new HashSet<>() : new HashSet<>(updatedIds);
//                    renderWithFilters(listView, actor);
//                });
//            });
//        });
//
//        binding.toggleStatus.check(R.id.toggle_status_all);
//        binding.toggleStatus.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
//            if (!isChecked) return;
//            if (checkedId == R.id.toggle_status_all) {
//                currentStatus = Status.ALL;
//            } else if (checkedId == R.id.toggle_status_joined) {
//                currentStatus = Status.JOINED;
//            } else if (checkedId == R.id.toggle_status_waitlisted) {
//                currentStatus = Status.WAITLISTED;
//            } else if (checkedId == R.id.toggle_status_selected) {
//                currentStatus = Status.SELECTED;
//            } else if (checkedId == R.id.toggle_status_not_selected) {
//                currentStatus = Status.NOT_SELECTED;
//            }
//            renderWithFilters(listView, actor);
//        });
//
//        binding.toggleAvailability.addOnButtonCheckedListener((group, id, isChecked) -> {
//            if (id == R.id.toggle_avail_weekdays) {
//                if (isChecked) selectedDayTypes.add("Weekdays"); else selectedDayTypes.remove("Weekdays");
//            } else if (id == R.id.toggle_avail_weekends) {
//                if (isChecked) selectedDayTypes.add("Weekends"); else selectedDayTypes.remove("Weekends");
//            }
//            renderWithFilters(listView, actor);
//        });
//
//        ChipGroup chips = binding.chipsInterests;
//        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
//            selectedCategories.clear();
//            for (Integer cid : checkedIds) {
//                Chip c = group.findViewById(cid);
//                if (c != null) selectedCategories.add(c.getText().toString());
//            }
//            renderWithFilters(listView, actor);
//        });
//
//        return root;
//    }
//
//    /**
//     * Resets all filter controls and in-memory selections to their default state.
//     */
//    private void clearAllFilters() {
//        binding.toggleStatus.check(R.id.toggle_status_all);
//        currentStatus = Status.ALL;
//
//        for (int i = 0; i < binding.toggleAvailability.getChildCount(); i++) {
//            View child = binding.toggleAvailability.getChildAt(i);
//            binding.toggleAvailability.uncheck(child.getId());
//        }
//        selectedDayTypes.clear();
//
//        binding.chipsInterests.clearCheck();
//        selectedCategories.clear();
//    }
//
//    /**
//     * Populates sidecar maps used for filtering and sorting without altering the {@link Event} model.
//     *
//     * @param events source events to tag
//     */
//
//    //The following part Idea is taken from: https://www.w3resource.com/java-exercises/constructor/java-constructor-exercise-1.php#google_vignette
//    // and https://stackoverflow.com/questions/16214685/is-it-possible-to-set-a-calendar-c-equal-calendar-c2
//    //Authored By: w3resource and ARMAGEDDON
//    //Taken By: Harmanjot Kaur Dhaliwal
//    //Taken on: November 6th, 2025
//    private void assignSidecarTagsForRealEvents(List<Event> events) {
//        dayTypeByEvent.clear();
//        categoryByEvent.clear();
//        timeByEvent.clear();
//
//        Calendar base = Calendar.getInstance();
//        for (int i = 0; i < events.size(); i++) {
//            Event e = events.get(i);
//
//            String day = (i % 2 == 0) ? "Weekdays" : "Weekends";
//            dayTypeByEvent.put(e, day);
//
//            String cat;
//            switch (i % 5) {
//                case 0: cat = "Swimming"; break;
//                case 1: cat = "Sports";   break;
//                case 2: cat = "Dance";    break;
//                case 3: cat = "Games";    break;
//                default: cat = "Music";   break;
//            }
//            categoryByEvent.put(e, cat);
//
//            Calendar c = (Calendar) base.clone();
//            c.add(Calendar.DAY_OF_YEAR, -i);
//            c.set(Calendar.HOUR_OF_DAY, 10);
//            c.set(Calendar.MINUTE, 0);
//            c.set(Calendar.SECOND, 0);
//            timeByEvent.put(e, c.getTimeInMillis());
//        }
//    }
//
//    /**
//     * Applies current filters to the live list, sorts if needed, and rebuilds the adapter.
//     *
//     * @param listView target list view to update
//     * @param actor    current actor for contextual filtering
//     */
//    private void renderWithFilters(ListView listView, Actor actor) {
//        List<Event> filtered = new ArrayList<>();
//        for (Event e : allEventsLive) {
//            if (!matchesStatus(e)) continue;
//            if (!matchesAvailability(e)) continue;
//            if (!matchesCategory(e)) continue;
//            filtered.add(e);
//        }
//
//        if (currentStatus == Status.JOINED) {
//            Collections.sort(filtered, new Comparator<Event>() {
//                @Override
//                public int compare(Event a, Event b) {
//                    long ta = timeByEvent.containsKey(a) ? timeByEvent.get(a) : 0L;
//                    long tb = timeByEvent.containsKey(b) ? timeByEvent.get(b) : 0L;
//                    return Long.compare(tb, ta);
//                }
//            });
//        }
//
//        EventArrayAdapter adapter =
//                new EventArrayAdapter(requireContext(), filtered, joinedEventIdsLive, SM, actor);
//        listView.setAdapter(adapter);
//
//        if (emptyView != null) {
//            if (currentStatus == Status.JOINED) {
//                emptyView.setText("You haven’t joined any events yet.");
//            } else {
//                emptyView.setText("No events match your filters.");
//            }
//        }
//    }
//
//    /**
//     * Checks whether an event matches the currently selected status filter.
//     *
//     * @param e event to evaluate
//     * @return true if the event matches the active status constraint; otherwise false
//     */
//    private boolean matchesStatus(Event e) {
//        if (currentStatus == Status.ALL) return true;
//
//        boolean joined = joinedEventIdsLive.contains(e.getId());
//        switch (currentStatus) {
//            case JOINED:
//                return joined;
//            case NOT_SELECTED:
//                return !joined;
//            case WAITLISTED:
//            case SELECTED:
//                return false;
//            default:
//                return true;
//        }
//    }
//
//    /**
//     * Checks whether an event matches the currently selected availability constraints.
//     *
//     * @param e event to evaluate
//     * @return true if availability matches or no availability is selected; otherwise false
//     */
//    private boolean matchesAvailability(Event e) {
//        if (selectedDayTypes.isEmpty()) return true;
//        String day = dayTypeByEvent.get(e);
//        return day != null && selectedDayTypes.contains(day);
//    }
//
//    /**
//     * Checks whether an event matches the currently selected category constraints.
//     *
//     * @param e event to evaluate
//     * @return true if category matches or no category is selected; otherwise false
//     */
//    private boolean matchesCategory(Event e) {
//        if (selectedCategories.isEmpty()) return true;
//        String cat = categoryByEvent.get(e);
//        return cat != null && selectedCategories.contains(cat);
//    }
//
//    /**
//     * Creates and attaches a centered {@link TextView} as the empty view for the list.
//     *
//     * @param initialText text to display when the list has no items
//     */
//
//    //The following part Idea is taken from: https://stackoverflow.com/questions/40275152/how-to-programmatically-add-views-and-constraints-to-a-constraintlayout?utm_source=chatgpt.com
//    //Authored By: LeoColman
//    //Taken By: Harmanjot Kaur Dhaliwal
//    //Taken on: November 6th, 2025
//    private void attachEmptyViewToList(String initialText) {
//        if (!(binding.getRoot() instanceof ConstraintLayout)) return;
//        ConstraintLayout root = (ConstraintLayout) binding.getRoot();
//
//        emptyView = new TextView(requireContext());
//        emptyView.setId(View.generateViewId());
//        emptyView.setText(initialText);
//        emptyView.setTextColor(getResources().getColor(android.R.color.darker_gray));
//        emptyView.setTypeface(Typeface.DEFAULT_BOLD);
//        emptyView.setGravity(Gravity.CENTER);
//        emptyView.setTextSize(16f);
//        int pad = (int) (16 * getResources().getDisplayMetrics().density);
//        emptyView.setPadding(pad, pad, pad, pad);
//
//        root.addView(emptyView, new ConstraintLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        ConstraintSet cs = new ConstraintSet();
//        cs.clone(root);
//        cs.connect(emptyView.getId(), ConstraintSet.TOP,    R.id.filters_quick_bar, ConstraintSet.BOTTOM, pad);
//        cs.connect(emptyView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, pad);
//        cs.connect(emptyView.getId(), ConstraintSet.START,  ConstraintSet.PARENT_ID, ConstraintSet.START, pad);
//        cs.connect(emptyView.getId(), ConstraintSet.END,    ConstraintSet.PARENT_ID, ConstraintSet.END, pad);
//        cs.applyTo(root);
//    }
//
//    /**
//     * Cleans up listeners and view bindings when the fragment view is destroyed.
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (registrationListener != null) {
//            registrationListener.remove();
//            registrationListener = null;
//        }
//        binding = null;
//    }
//}
