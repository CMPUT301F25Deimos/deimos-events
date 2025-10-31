package com.example.deimos_events.ui.qr_code;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.databinding.FragmentQrCodeBinding;

public class QRCodeFragment extends Fragment {
    
    private FragmentQrCodeBinding binding;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QRCodeViewModel qrCodeViewModel =
                new ViewModelProvider(this).get(QRCodeViewModel.class);
        
        binding = FragmentQrCodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        final TextView textView = binding.textQrCode;
        qrCodeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}