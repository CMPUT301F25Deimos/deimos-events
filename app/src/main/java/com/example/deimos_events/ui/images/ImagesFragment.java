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

import com.example.deimos_events.dataclasses.Event;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.databinding.FragmentAdministratorsImagesBinding;
import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.managers.SessionManager;

/**
 * Fragment that admins use to view and manage the images uploaded
 */
public class ImagesFragment extends Fragment {
    private FragmentAdministratorsImagesBinding binding;
    private SessionManager SM;
    private EventManager EM;
    private ImagesAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Grabs the session manager and event manager
        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        EM = SM.getEventManager();
    }

    /**
     * Creates and initializes the UI for the fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container Optional parent view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The root view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAdministratorsImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerAdminImages;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ImagesAdapter();
        recyclerView.setAdapter(adapter);

        //Gets all the events from the database and fills the adapter
        EM.getAllEvents(events->{
            if(getActivity() == null)return;

            requireActivity().runOnUiThread(()->adapter.submitList(events));



        });
        //Adds a swipe to delete function
        ItemTouchHelper helper = new ItemTouchHelper(createSwipeCallback());
        helper.attachToRecyclerView(recyclerView);
        
        return root;
    }

    /**
     * Creates swipe to delete callback for removing images
     * @return A SimpleCallback that handles left swipe
     */
    private ItemTouchHelper.SimpleCallback createSwipeCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Handles the swiping to delete an image
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

                //deleting it from the database
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
