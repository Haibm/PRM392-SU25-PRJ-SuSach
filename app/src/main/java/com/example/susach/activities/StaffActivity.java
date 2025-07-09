package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import com.example.susach.databinding.ActivityStaffBinding;
import androidx.appcompat.app.AppCompatActivity;

public class StaffActivity extends AppCompatActivity {
    private ActivityStaffBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnManageArticle.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageArticleActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> finish());
    }
}