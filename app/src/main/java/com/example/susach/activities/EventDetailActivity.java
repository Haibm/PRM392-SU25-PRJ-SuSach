package com.example.susach.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.susach.databinding.ActivityEventDetailBinding;
import com.example.susach.managers.EventManager;
import com.example.susach.models.Event;

public class EventDetailActivity extends AppCompatActivity {
    private ActivityEventDetailBinding binding;
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventManager = new EventManager();
        String eventId = getIntent().getStringExtra("event_id");
        loadEventDetail(eventId);
    }

    private void loadEventDetail(String eventId) {
        eventManager.getEventById(eventId, new EventManager.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                if (event != null) {
                    binding.tvName.setText(event.getName());
                    binding.tvDescription.setText(event.getDescription());
                    binding.tvContents.setText(event.getContents());
                    binding.tvStartDate.setText(String.valueOf(event.getStartDate()));
                    binding.tvEndDate.setText(String.valueOf(event.getEndDate()));
                    binding.tvImageUrl.setText(event.getImageUrl());
                    binding.tvSummary.setText(event.getSummary());
                    binding.tvImageContent.setText(event.getImageContent());
                    binding.tvCreateBy.setText(event.getCreateBy());
                    binding.tvUpdateBy.setText(event.getUpdateBy());
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EventDetailActivity.this, "Lỗi tải chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
} 