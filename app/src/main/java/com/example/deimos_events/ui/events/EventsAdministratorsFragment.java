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
 * Fragment for administrators to view and manage all events.
 * <p>
 * Displays all events in a {@link RecyclerView} with title, description, and photo,
 * and allows the admin to delete an event using a swipe-to-delete gesture.
 */
public class EventsAdministratorsFragment extends Fragment {

    private FragmentAdministratorsEventsBinding binding;
    private SessionManager sessionManager;
    private EventManager eventManager;
    private AdministratorsEventsAdapter adapter;

    private final Paint swipePaint = new Paint();

    /**
     * Called to do initial creation of the fragment.
     * <p>
     * Initializes the {@link SessionManager} and {@link EventManager} from the application
     * {@link EventsApp} instance.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventsApp app = (EventsApp) requireActivity().getApplicationContext();
        sessionManager = app.getSessionManager();
        eventManager = sessionManager.getEventManager();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * <p>
     * Sets up the {@link RecyclerView}, attaches the adapter, loads all events from
     * the {@link EventManager}, and configures swipe-to-delete behavior.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The root View for the fragment's UI.
     */
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

    /**
     * Creates the {@link ItemTouchHelper.SimpleCallback} used to enable
     * swipe-to-delete behavior on the admin events list.
     * <p>
     * A left swipe draws a red background with a delete icon and removes
     * the event when the swipe is completed.
     *
     * @return a configured {@link ItemTouchHelper.SimpleCallback} for left swipes
     */
    private ItemTouchHelper.SimpleCallback createSwipeCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            /**
             * Drag & drop move handling is disabled for this list.
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Called when an item has been swiped left.
             * <p>
             * Retrieves the swiped {@link Event} and delegates deletion to
             * {@link EventManager#adminDeleteEvent(Event, java.util.function.Consumer)}.
             * On success, removes the item from the adapter and shows a toast.
             *
             * @param viewHolder The ViewHolder which has been swiped by the user.
             * @param direction  The direction to which the ViewHolder is swiped.
             */
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

            /**
             * Called by the {@link ItemTouchHelper} on RecyclerView's onDraw callback.
             * <p>
             * Draws the red background and delete icon while the item is being swiped.
             *
             * @param c                 Canvas to draw on.
             * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached.
             * @param viewHolder        The ViewHolder being interacted by the user.
             * @param dX                Horizontal displacement caused by the user's action.
             * @param dY                Vertical displacement caused by the user's action.
             * @param actionState       Type of interaction on the View.
             * @param isCurrentlyActive True if the user is currently controlling the item.
             */
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
    /**
     * Called when the view hierarchy associated with the fragment is being removed.
     * <p>
     * Clears the view binding reference to avoid memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
