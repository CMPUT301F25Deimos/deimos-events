package com.example.deimos_events.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deimos_events.Actor;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.IDatabase;
import com.example.deimos_events.R;
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


        actorManager.getAllActors(actors->{
            if(getActivity() == null)return;

            requireActivity().runOnUiThread(()->adapter.submitList(actors));



        });

        return root;
    }


}
