package com.example.deimos_events.ui.events;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TextView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentEventsBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.deimos_events.ui.notifications.NotificationsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;
// import com.example.deimos_events.EventManager;   // uncomment when ready
// import com.example.deimos_events.NavigationManager;
// import com.example.deimos_events.Result;
// import com.example.deimos_events.models.Event;
// import com.example.deimos_events.models.Registration;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;

    private SessionManager SM;
    private UserInterfaceManager UIM;
    // private EventManager EM;
    // private NavigationManager NM;

    private final ArrayList<EventTest> allEvents = new ArrayList<>();
    private final ArrayList<EventTest> visibleEvents = new ArrayList<>();
    private EventArrayAdapter adapter;

    // Sidecar metadata (so we don’t modify EventTest)
    private final Map<EventTest, String> dayTypeByEvent = new HashMap<>(); // "Weekdays"/"Weekends"
    private final Map<EventTest, String> categoryByEvent = new HashMap<>(); // "Swimming"/"Sports"/...
    private final Map<EventTest, Long>   timeByEvent = new HashMap<>(); // epoch millis for sorting

    // Filter state
    private enum Status { ALL, JOINED, WAITLISTED, SELECTED, NOT_SELECTED }
    private Status currentStatus = Status.ALL;
    private final Set<String> selectedDayTypes = new HashSet<>();
    private final Set<String> selectedCategories = new HashSet<>();

    // Programmatic empty view so we don’t change your XML
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Grab managers if available (non-fatal if not wired yet)
        try {
            EventsApp app = (EventsApp) requireActivity().getApplicationContext();
            SM = app.getSessionManager();
            if (SM != null) {
                UIM = SM.getUserInterfaceManager();
                // NM = UIM.getNavigationManager();
                // EM = SM.getEventManager(); // uncomment when ready
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadEvents();


        final FloatingActionButton add  = binding.filterFab;
        add.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);

            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_events,true).build();
            navController.navigate(R.id.navigation_create, null , navOptions);
        });



        // data to use in the list
        ArrayList<EventTest> eventsList = new ArrayList<>();
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, -1, true));
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false,-1, false));
        eventsList.add(new EventTest("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", R.drawable.img, true, 0, false));
        eventsList.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, 0, false));
        
        ListView listView = binding.eventsList;
        visibleEvents.clear();
        visibleEvents.addAll(allEvents);
        adapter = new EventArrayAdapter(requireContext(), visibleEvents);
        listView.setAdapter(adapter);

        attachEmptyViewToList("You haven’t joined any events yet.");
        listView.setEmptyView(emptyView);


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
            applyFilters();
        });

        binding.toggleAvailability.addOnButtonCheckedListener((group, id, isChecked) -> {
            if (id == R.id.toggle_avail_weekdays) {
                if (isChecked) selectedDayTypes.add("Weekdays"); else selectedDayTypes.remove("Weekdays");
            } else if (id == R.id.toggle_avail_weekends) {
                if (isChecked) selectedDayTypes.add("Weekends"); else selectedDayTypes.remove("Weekends");
            }
            applyFilters();
        });

        ChipGroup chips = binding.chipsInterests;
        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedCategories.clear();
            for (Integer cid : checkedIds) {
                Chip c = group.findViewById(cid);
                if (c != null) selectedCategories.add(c.getText().toString());
            }
            applyFilters();
        });

        View clearBtn = binding.buttonClearFilters; // present in your XML
        if (clearBtn != null) {
            clearBtn.setOnClickListener(v -> clearAllFilters());
        }
        binding.filterFab.setOnClickListener(v -> clearAllFilters());

        applyFilters();
        return root;
    }

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

        applyFilters();
        Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadEvents() {
        // ===== Uncomment when EventManager is available =====
        // if (EM != null) {
        //     String userId = (UIM != null && UIM.getCurrentActor() != null) ? UIM.getCurrentActor().getId() : null;
        //     EM.getAllEvents(result -> {
        //         if (result.isSuccess()) {
        //             List<Event> events = (List<Event>) result.getData();
        //             mapEventsToUI(events);
        //             assignSidecarTags(allEvents);
        //             applyFilters();
        //         } else {
        //             Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        //             seedDemo();
        //             assignSidecarTags(allEvents);
        //         }
        //     });
        //     return;
        // }

        // Fallback demo data so screen runs now
        seedDemo();
        assignSidecarTags(allEvents);
    }

    private void seedDemo() {
        ArrayList<EventTest> demo = new ArrayList<>();
        demo.add(new EventTest(lipsumLong(), R.drawable.img, true, 0, false));   // selected (pending)
        demo.add(new EventTest("HEllooooooooooooooooooooooooooo", R.drawable.join_sticker_24dp, false, -1, true)); // organizer-owned, waitlisted
        demo.add(new EventTest(lipsumLong(), R.drawable.img, true, 0, false));
        demo.add(new EventTest("Short one", R.drawable.join_sticker_24dp, false, -1, false));
        demo.add(new EventTest(lipsumLong(), R.drawable.img, true, 0, false));
        demo.add(new EventTest("Another", R.drawable.join_sticker_24dp, false, 0, false));

        allEvents.clear();
        allEvents.addAll(demo);
    }

    private void assignSidecarTags(List<EventTest> events) {
        dayTypeByEvent.clear();
        categoryByEvent.clear();
        timeByEvent.clear();

        Calendar base = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            EventTest e = events.get(i);

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

    private void applyFilters() {
        List<EventTest> next = new ArrayList<>();
        for (EventTest e : allEvents) {
            if (!matchesStatus(e)) continue;
            if (!matchesAvailability(e)) continue;
            if (!matchesCategory(e)) continue;
            next.add(e);
        }

        if (currentStatus == Status.JOINED) {
            Collections.sort(next, new Comparator<EventTest>() {
                @Override
                public int compare(EventTest a, EventTest b) {
                    long ta = timeByEvent.containsKey(a) ? timeByEvent.get(a) : 0L;
                    long tb = timeByEvent.containsKey(b) ? timeByEvent.get(b) : 0L;
                    return Long.compare(tb, ta); // desc
                }
            });
        }

        visibleEvents.clear();
        visibleEvents.addAll(next);
        adapter.notifyDataSetChanged();

        if (emptyView != null) {
            if (currentStatus == Status.JOINED) {
                emptyView.setText("You haven’t joined any events yet.");
            } else {
                emptyView.setText("No events match your filters.");
            }
        }
    }

    private boolean matchesStatus(EventTest e) {
        if (currentStatus == Status.ALL) return true;

        boolean joined = e.getWaitingList();
        int wta = e.getWaitingToAccept(); // -1 waitlisted, 0 selected-pending, 1 accepted, 2 declined

        switch (currentStatus) {
            case JOINED:
                return joined;
            case WAITLISTED:
                return wta == -1;
            case SELECTED:
                return (wta == 0 || wta == 1);
            case NOT_SELECTED:
                if (wta == 2) return true;
                return !joined && (wta != -1 && wta != 0 && wta != 1);
            default:
                return true;
        }
    }

    private boolean matchesAvailability(EventTest e) {
        if (selectedDayTypes.isEmpty()) return true;
        String day = dayTypeByEvent.get(e);
        return day != null && selectedDayTypes.contains(day);
    }

    private boolean matchesCategory(EventTest e) {
        if (selectedCategories.isEmpty()) return true;
        String cat = categoryByEvent.get(e);
        return cat != null && selectedCategories.contains(cat);
    }

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

    private String lipsumLong() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}