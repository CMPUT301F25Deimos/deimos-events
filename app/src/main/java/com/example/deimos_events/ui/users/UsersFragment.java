package com.example.deimos_events.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.databinding.FragmentAdministratorsUsersBinding;

public class UsersFragment extends Fragment {
    private FragmentAdministratorsUsersBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAdministratorsUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        return root;
    }
}
