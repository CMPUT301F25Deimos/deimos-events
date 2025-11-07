package com.example.deimos_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanOptions;

public class QrScannerActivity extends AppCompatActivity {
    Button scanButton;
    private SessionManager SM;
    private UserInterfaceManager UIM;
    private NavigationManager NM;
    private EventManager EM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        SM = ((EventsApp) getApplicationContext()).getSessionManager();
        UIM = SM.getUserInterfaceManager();
        NM = UIM.getNavigationManager();
        EM = SM.getEventManager();
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v->{
            startQrScanner();
        });


    }

    private void startQrScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.initiateScan();
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null){
            if (result.getContents() != null){
                String scannedData = result.getContents();

                EM.getEventById(scannedData, event->{
                    if(event != null){
                        UIM.setSelectedEvent(event);

                        NM.goTo(EventInformationActivity.class);
                    }else{
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


}
