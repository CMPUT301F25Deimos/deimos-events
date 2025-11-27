package com.example.deimos_events.ui.images;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.databinding.FragmentAdministratorsImagesBinding;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.ui.users.UsersAdapter;

public class ImagesFragment extends Fragment {
    private FragmentAdministratorsImagesBinding binding;
    private SessionManager SM;
    private EventManager EM;
    private ImagesAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        EM = SM.getEventManager();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAdministratorsImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerAdminImages;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ImagesAdapter();
        recyclerView.setAdapter(adapter);


        EM.getAllEvents(events->{
            if(getActivity() == null)return;

            requireActivity().runOnUiThread(()->adapter.submitList(events));



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
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getBindingAdapterPosition();
                Event event = adapter.getEventAt(pos);
                if (event == null) {
                    adapter.notifyItemChanged(pos);
                    return;
                }

                EM.deleteEventImage(event.getId(), result -> {
                    if (getActivity() == null) return;
                    requireActivity().runOnUiThread(() -> {
                        adapter.removeImage(pos);
                        Toast.makeText(requireContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        };
    }


}
