package com.example.susach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {
    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        recyclerEvents = findViewById(R.id.recycler_events);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList, event -> openEventDetail(event));
        recyclerEvents.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        loadEvents();
        // Đã xóa code xử lý BottomNavigationView
    }

    private void loadEvents() {
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Event event = doc.toObject(Event.class);
                if (event != null) eventList.add(event);
            }
            eventAdapter.setEventList(eventList);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }
} 