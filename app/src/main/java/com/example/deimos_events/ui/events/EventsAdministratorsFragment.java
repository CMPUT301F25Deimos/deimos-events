package com.example.deimos_events.ui.events;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentAdministratorsEventsBinding;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

/**
 * Admin screen: shows ALL events with title + description + photo and lets admin delete them.
 */
public class EventsAdministratorsFragment extends Fragment {

    private FragmentAdministratorsEventsBinding binding;
    private SessionManager sessionManager;
    private EventManager eventManager;
    private AdministratorsEventsAdapter adapter;

    private final Paint swipePaint = new Paint();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        sessionManager = app.getSessionManager();
        eventManager = sessionManager.getEventManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdministratorsEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerAdminEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AdministratorsEventsAdapter();
        recyclerView.setAdapter(adapter);

        eventManager.getAllEvents(events -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> adapter.submitList(events));
        });

        ItemTouchHelper helper = new ItemTouchHelper(createSwipeCallback());
        helper.attachToRecyclerView(recyclerView);

        return root;
    }

    private ItemTouchHelper.SimpleCallback createSwipeCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getBindingAdapterPosition();
                Event event = adapter.getEventAt(pos);
                if (event == null) {
                    adapter.notifyItemChanged(pos);
                    return;
                }

                eventManager.adminDeleteEvent(event, result -> {
                    if (getActivity() == null) return;
                    requireActivity().runOnUiThread(() -> {
                        adapter.removeEvent(pos);
                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    });
                });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {

                    swipePaint.setColor(ContextCompat.getColor(requireContext(),
                            android.R.color.holo_red_dark));
                    RectF background = new RectF(
                            itemView.getRight() + dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    c.drawRect(background, swipePaint);

                    android.graphics.drawable.Drawable icon =
                            ContextCompat.getDrawable(requireContext(),
                                    android.R.drawable.ic_menu_delete);
                    if (icon != null) {
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = icon.getIntrinsicWidth();
                        int intrinsicHeight = icon.getIntrinsicHeight();

                        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int iconBottom = iconTop + intrinsicHeight;
                        int iconMargin = (itemHeight - intrinsicHeight) / 2;
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - intrinsicWidth;

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
