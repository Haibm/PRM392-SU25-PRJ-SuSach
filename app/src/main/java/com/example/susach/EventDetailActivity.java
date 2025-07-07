package com.example.susach;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.susach.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {
    private TextView tvName, tvDescription, tvContents, tvStartDate, tvEndDate, tvImageUrl, tvSummary, tvImageContent, tvCreateBy, tvUpdateBy;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
        tvContents = findViewById(R.id.tvContents);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvImageUrl = findViewById(R.id.tvImageUrl);
        tvSummary = findViewById(R.id.tvSummary);
        tvImageContent = findViewById(R.id.tvImageContent);
        tvCreateBy = findViewById(R.id.tvCreateBy);
        tvUpdateBy = findViewById(R.id.tvUpdateBy);

        db = FirebaseFirestore.getInstance();
        String eventId = getIntent().getStringExtra("event_id");
        loadEventDetail(eventId);
    }

    private void loadEventDetail(String eventId) {
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    tvName.setText(event.getName());
                    tvDescription.setText(event.getDescription());
                    tvContents.setText(event.getContents());
                    tvStartDate.setText(String.valueOf(event.getStartDate()));
                    tvEndDate.setText(String.valueOf(event.getEndDate()));
                    tvImageUrl.setText(event.getImageUrl());
                    tvSummary.setText(event.getSummary());
                    tvImageContent.setText(event.getImageContent());
                    tvCreateBy.setText(event.getCreateBy());
                    tvUpdateBy.setText(event.getUpdateBy());
                }
            } else {
                Toast.makeText(this, "Không tìm thấy sự kiện!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
} 