package com.example.susach.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.susach.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: binding logic cho profile (hiển thị thông tin user, logout, ...)
    }
}