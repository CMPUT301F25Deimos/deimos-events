package com.example.deimos_events.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deimos_events.MainActivity;
import com.example.deimos_events.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etDeviceId, etName, etEmail, etPhone;
    private MaterialButton btnSignup, btnMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etDeviceId = findViewById(R.id.et_device_id);
        etName     = findViewById(R.id.et_name);
        etEmail    = findViewById(R.id.et_email);
        etPhone    = findViewById(R.id.et_phone);
        btnSignup  = findViewById(R.id.btn_signup);
        btnMore    = findViewById(R.id.btn_more);

        try {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(id)) etDeviceId.setText(id);
        } catch (Exception ignored) {}

        btnSignup.setOnClickListener(v -> submit());
        btnMore.setOnClickListener(v -> Toast.makeText(this, "Go to Login screen", Toast.LENGTH_SHORT).show());
    }

    private void submit() {
        String deviceId = safe(etDeviceId);
        String name     = safe(etName);
        String email    = safe(etEmail);
        String phone    = safe(etPhone);

        if (deviceId.isEmpty()) {
            toast("Device ID required"); return;
        }
        if (name.isEmpty()) {
            toast("Name required"); return;
        }
        if (email.isEmpty()) {
            toast("Email required"); return;
        }

        // Mark as signed up
        getSharedPreferences("app", MODE_PRIVATE)
                .edit()
                .putBoolean("signed_up", true)
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String safe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
