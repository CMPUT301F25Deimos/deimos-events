package com.example.deimos_events.ui.users;

import static android.content.ContentValues.TAG;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.dataclasses.Actor;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
import com.example.deimos_events.Session;
import com.example.deimos_events.databinding.FragmentAdministratorsUsersBinding;
import com.example.deimos_events.managers.ActorManager;
import com.example.deimos_events.managers.SessionManager;

import java.util.ArrayList;
import java.util.List;
/**
 * Fragment that allows administrators to view, filter, and delete all users (Actors)
 * registered in the system.
 *
 * <p>This screen displays a RecyclerView of all actors loaded from Firestore via
 * {@link ActorManager}. Administrators may:</p>
 *
 * <ul>
 *     <li>Filter users by role (All, Entrants, Organizers)</li>
 *     <li>Swipe left on a user to delete them</li>
 *     <li>See live updates to the list after deleting or reloading users</li>
 * </ul>
 *
 * <p>The fragment maintains an internal list of all actors and applies filters to
 * present the correct subset. Swipe-to-delete functionality is implemented using
 * an {@link ItemTouchHelper.SimpleCallback} with a red background and delete icon.</p>
 */
public class UsersFragment extends Fragment {

    /**
     * View binding for the administrators users layout.
     */
    private FragmentAdministratorsUsersBinding binding;

    /**
     * Session manager used to access user session and managers.
     */
    private SessionManager SM;

    /**
     * Adapter used for displaying user entries in the RecyclerView.
     */
    private UsersAdapter adapter;

    /**
     * Manager class responsible for user-related database operations.
     */
    private ActorManager actorManager;

    /**
     * Stores all actors loaded from the database.
     * Filtering is applied to this list rather than reloading from DB.
     */
    private final List<Actor> allActors = new ArrayList<>();

    /**
     * Filters available for user roles.
     */
    private enum UserFilter { ALL, ENTRANTS, ORGANIZERS }

    /**
     * Tracks the currently selected filter for the UI.
     */
    private UserFilter currentFilter = UserFilter.ALL;

    /**
     * Initializes the fragment, retrieving the session manager and actor manager.
     *
     * @param savedInstanceState previous saved state, if available
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        actorManager = SM.getActorManager();
    }
    /**
     * Inflates the view, initializes UI components, loads all actors,
     * applies the default filter, and sets up swipe-to-delete behavior.
     *
     * @param inflater  LayoutInflater to inflate the fragment layout
     * @param container Parent container of the fragment
     * @param savedInstanceState saved state if available
     * @return the fully constructed root view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdministratorsUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerAdminUsers;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new UsersAdapter();
        recyclerView.setAdapter(adapter);

        binding.toggleUserFilter.check(R.id.toggle_users_all);  // default selection

        binding.toggleUserFilter.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;  // ignore uncheck events

            if (checkedId == R.id.toggle_users_all) {
                currentFilter = UserFilter.ALL;
            } else if (checkedId == R.id.toggle_users_entrants) {
                currentFilter = UserFilter.ENTRANTS;
            } else if (checkedId == R.id.toggle_users_organizers) {
                currentFilter = UserFilter.ORGANIZERS;
            }

            applyCurrentFilter();
        });

        actorManager.getAllActors(actors -> {
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> {
                allActors.clear();
                if (actors != null) {
                    allActors.addAll(actors);
                }
                applyCurrentFilter();
            });
        });

        attachSwipeToDelete(recyclerView);

        return root;
    }
    /**
     * Filters the list of loaded actors based on the currently selected user filter
     * and updates the adapter.
     */
    private void applyCurrentFilter() {
        List<Actor> filtered = new ArrayList<>();

        for (Actor a : allActors) {
            String role = "";
            if (a.getRole() != null) {
                // normalize: trim spaces + lower case
                role = a.getRole().trim().toLowerCase();
            }

            switch (currentFilter) {
                case ALL:
                    filtered.add(a);
                    break;

                case ENTRANTS:
                    if (role.equals("entrant")) {
                        filtered.add(a);
                    }
                    break;

                case ORGANIZERS:
                    if (role.equals("organizer")) {
                        filtered.add(a);
                    }
                    break;
            }
        }

        adapter.submitList(filtered);
    }

    /**
     * Attaches swipe-to-delete functionality to a RecyclerView.
     *
     * <p>Swiping left causes a delete icon and red background to appear.
     * When released, the actor is removed locally and deleted from the database.
     * If deletion fails, the list is reloaded to stay consistent.</p>
     *
     * @param recyclerView the RecyclerView to attach delete gestures to
     */
    private void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    private final Paint redPaint = new Paint();
                    private final Drawable deleteIcon =
                            ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete);

                    {
                        redPaint.setColor(Color.parseColor("#D32F2F")); // red background
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView rv,
                                          @NonNull RecyclerView.ViewHolder vh,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false; // no drag & drop
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getBindingAdapterPosition();
                        Actor actor = adapter.getActorAt(position);
                        if (actor == null) {
                            adapter.notifyItemChanged(position);
                            return;
                        }

                        // remove from local list first
                        adapter.removeAt(position);
                        allActors.remove(actor); // keep allActors in sync

                        Session session = SM.getSession();
                        ActorManager AM = SM.getActorManager();

                        AM.deleteActor(actor, result -> {
                            if (getActivity() == null) return;
                            requireActivity().runOnUiThread(() -> {
                                if (result.isSuccess()) {
                                    Toast.makeText(requireContext(),
                                            "User deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(),
                                            "Failed to delete user", Toast.LENGTH_SHORT).show();
                                    // reload if delete failed
                                    actorManager.getAllActors(actors -> {
                                        if (getActivity() == null) return;
                                        requireActivity().runOnUiThread(() -> {
                                            allActors.clear();
                                            if (actors != null) allActors.addAll(actors);
                                            applyCurrentFilter();
                                        });
                                    });
                                }
                            });
                        });
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        if (dX == 0 && !isCurrentlyActive) {
                            super.onChildDraw(c, rv, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            return;
                        }

                        float left = itemView.getRight() + dX;
                        float right = itemView.getRight();
                        RectF background = new RectF(left, itemView.getTop(), right, itemView.getBottom());
                        c.drawRect(background, redPaint);

                        if (deleteIcon != null) {
                            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                            int iconRight = itemView.getRight() - iconMargin;
                            int iconTop = itemView.getTop() + iconMargin;
                            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            deleteIcon.draw(c);
                        }

                        super.onChildDraw(c, rv, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}
