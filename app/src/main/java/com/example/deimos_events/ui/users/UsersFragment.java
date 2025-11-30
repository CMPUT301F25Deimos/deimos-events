package com.example.deimos_events.ui.users;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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

public class UsersFragment extends Fragment {

    private FragmentAdministratorsUsersBinding binding;
    private SessionManager SM;
    private UsersAdapter adapter;
    private ActorManager actorManager;

    // keep ALL actors here
    private final List<Actor> allActors = new ArrayList<>();

    private enum UserFilter { ALL, ENTRANTS, ORGANIZERS }
    private UserFilter currentFilter = UserFilter.ALL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        actorManager = SM.getActorManager();
    }

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
     * Same swipe-to-delete behaviour as before – unchanged, just moved into this class.
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
                        IDatabase db = session.getDatabase();
                        db.deleteActor(actor, success -> {
                            if (getActivity() == null) return;
                            requireActivity().runOnUiThread(() -> {
                                if (Boolean.TRUE.equals(success)) {
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
