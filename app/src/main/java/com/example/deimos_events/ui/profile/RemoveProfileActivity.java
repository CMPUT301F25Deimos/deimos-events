package com.example.deimos_events.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.R;

public class RemoveProfileActivity extends AppCompatActivity {

    private View confirmBtn;
    private View returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🔹 Make sure your XML file is named res/layout/remove_profile.xml
        setContentView(R.layout.remove_profile);

        confirmBtn = findViewById(R.id.confirm_delete_btn);
        returnBtn  = findViewById(R.id.return_btn);

        returnBtn.setOnClickListener(v -> finish());

        confirmBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Deleting profile…", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }
}
