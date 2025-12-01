package com.example.deimos_events.ui.qr_code;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deimos_events.managers.EventManager;
import com.example.deimos_events.EventsApp;
import com.example.deimos_events.managers.NavigationManager;
import com.example.deimos_events.R;
import com.example.deimos_events.managers.SessionManager;
import com.example.deimos_events.managers.UserInterfaceManager;
import com.example.deimos_events.ui.events.EventInformationActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

/**
 * Fragment scans QR code and send entrant to the event information screen
 */

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

        //When scan button is pressed then QR scanning starts
        scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v->{startQrScanner();});

        return view;
    }

    /**
     * Launches the QR code scanner using IntentIntergrator
     * Opens the camera interface allowing the user to scan event QR
     */
    private void startQrScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.initiateScan();
    }

    /**
     * Handles the resulting information from scanning the QR scanning activity
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null){
            String scannedData = result.getContents();
            EM.fetchEventById(scannedData, res->{
                if(!res.isNull()){
                    // succeeded, the event has been placed into the session
                    NM.goTo(EventInformationActivity.class, NavigationManager.navFlags.NO_FLAGS);
                }else{
                    Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(requireContext(), "No QR code detected", Toast.LENGTH_SHORT).show();
        }
    }

}