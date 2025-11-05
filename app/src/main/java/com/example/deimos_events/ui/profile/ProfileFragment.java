package com.example.deimos_events.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.deimos_events.R;
import com.example.deimos_events.databinding.FragmentProfileBinding;

import java.text.DateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    private static final String SP = "entrant_prefs";
    private static final String KEY_NOTIFY = "receive_notifications";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        profileViewModel.getProfile().observe(getViewLifecycleOwner(), this::bindProfileCard);

        SharedPreferences sp = requireContext().getSharedPreferences(SP, Context.MODE_PRIVATE);
        boolean initial = sp.getBoolean(KEY_NOTIFY, true);
        if (binding.notifySwitch != null) {
            binding.notifySwitch.setChecked(initial);
            binding.notifySwitch.setOnCheckedChangeListener((btn, checked) ->
                    sp.edit().putBoolean(KEY_NOTIFY, checked).apply());
        }

        if (binding.updateButton != null) {
            binding.updateButton.setOnClickListener(v -> showInlineEditDialog());
        }

        if (binding.deleteButton != null) {
            binding.deleteButton.setOnClickListener(v -> showDeleteDialog());
        }

        if (binding.roleText != null) binding.roleText.setText("Role: Entrant");
        if (binding.joinedText != null) {
            binding.joinedText.setText("Joined: " + DateFormat.getDateInstance().format(new Date()));
        }

        return root;
    }

    private void bindProfileCard(Profile p) {
        if (p == null) return;
        if (binding.nameText != null) binding.nameText.setText(p.getName());
        if (binding.emailText != null) binding.emailText.setText("Email: " + p.getEmail());
        if (binding.phoneText != null) {
            binding.phoneText.setText("Phone Number: " +
                    (TextUtils.isEmpty(p.getPhone()) ? "—" : p.getPhone()));
        }
    }

    private void showInlineEditDialog() {
        Profile cur = profileViewModel.getProfile().getValue();

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        EditText etName  = new EditText(requireContext()); etName.setHint("Full name");
        EditText etEmail = new EditText(requireContext()); etEmail.setHint("Email");
        EditText etPhone = new EditText(requireContext()); etPhone.setHint("Phone (optional)");

        if (cur != null) {
            etName.setText(cur.getName());
            etEmail.setText(cur.getEmail());
            etPhone.setText(cur.getPhone());
        }

        container.addView(etName,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = pad / 2;
        container.addView(etEmail, lp);
        container.addView(etPhone, lp);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", (d, w) -> {
                    String name  = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    if (name.isEmpty() || email.isEmpty()) {
                        Toast t = Toast.makeText(requireContext(), "Name and Email required", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                        return;
                    }

                    profileViewModel.updateProfile(name, email, phone);
                    Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog() {
        View content = LayoutInflater.from(requireContext())
                .inflate(R.layout.remove_profile, null, false);

        View confirmBtn = content.findViewById(R.id.confirm_delete_btn);
        View returnBtn  = content.findViewById(R.id.return_btn);

        if (confirmBtn == null || returnBtn == null) {
            throw new IllegalStateException("remove_profile.xml missing confirm/return button IDs.");
        }

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setView(content)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        returnBtn.setOnClickListener(v -> dialog.dismiss());

        confirmBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Deleting profile…", Toast.LENGTH_SHORT).show();

            profileViewModel.updateProfile("—", "—", "");
            dialog.dismiss();

            Toast.makeText(requireContext(), "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
