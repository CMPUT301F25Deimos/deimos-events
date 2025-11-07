package com.example.deimos_events.ui.qr_code;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.SessionManager;
import com.example.deimos_events.UserInterfaceManager;
import com.example.deimos_events.databinding.FragmentQrCodeBinding;
import com.example.deimos_events.ui.events.EventInformationActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeFragment extends Fragment {
    Button scanButton;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
    private EventManager EM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qr_scan, container, false);

        SM = ((EventsApp) requireActivity().getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NM = SM.getNavigationManager();
        EM = SM.getEventManager();

        scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v->{startQrScanner();});

        return view;
    }
    private void startQrScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null){
            String scannedData = result.getContents();
            EM.fetchEventById(scannedData, res->{
                if(!res.isNull()){
                    // succeeded, the event has been placed into the session
                    NM.goTo(EventInformationActivity.class, NavigationManager.navFlags.NO_FLAGS);//Fix this
                }else{
                    Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(requireContext(), "No QR code detected", Toast.LENGTH_SHORT).show();
        }
    }

}