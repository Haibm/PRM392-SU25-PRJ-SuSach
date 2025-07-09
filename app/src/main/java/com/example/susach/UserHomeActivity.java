package com.example.susach;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.susach.databinding.ActivityUserHomeBinding;
import com.example.susach.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

//Dat: UserHomeActivity - Trang chủ
public class UserHomeActivity extends AppCompatActivity {
    //Dat: View Binding cho Home
    private ActivityUserHomeBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Dat: Setup View Binding
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Dat: Setup RecyclerView cho Home
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList, event -> openEventDetail(event));
        binding.recyclerEvents.setAdapter(eventAdapter);

        //Dat: Setup BottomNavigationView cho Home
        setupBottomNavigation();

        //Dat: Load dữ liệu từ Firestore cho Home
        db = FirebaseFirestore.getInstance();
        loadEvents();
    }

    //Dat: Navigation menu chỉ xử lý chuyển trang hoặc Toast
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_explore) {
                Toast.makeText(this, "Tính năng Khám phá sẽ được phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_quiz) {
                Toast.makeText(this, "Tính năng Quiz sẽ được phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    //Dat: Load events cho Home
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