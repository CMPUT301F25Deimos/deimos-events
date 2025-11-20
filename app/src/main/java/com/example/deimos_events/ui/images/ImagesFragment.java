package com.example.deimos_events.ui.images;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.databinding.FragmentAdministratorsImagesBinding;

public class ImagesFragment extends Fragment {
    private FragmentAdministratorsImagesBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAdministratorsImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        return root;
    }
}
