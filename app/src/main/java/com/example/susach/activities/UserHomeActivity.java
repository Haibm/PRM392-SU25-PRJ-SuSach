package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.susach.R;
import com.example.susach.adapters.EventAdapter;
import com.example.susach.databinding.ActivityUserHomeBinding;
import com.example.susach.managers.EventManager;
import com.example.susach.models.Event;
import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {
    private ActivityUserHomeBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventAdapter = new EventAdapter(eventList, event -> openEventDetail(event));
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerEvents.setAdapter(eventAdapter);

        eventManager = new EventManager();
        loadEvents();

        //Dat: Xử lý chuyển trang cho BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Đang ở Home, không làm gì
                return true;
            } else if (itemId == R.id.navigation_explore) {
                // Chuyển sang ExploreActivity nếu có
                // startActivity(new Intent(this, ExploreActivity.class));
                Toast.makeText(this, "Tính năng Khám phá sẽ được phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_quiz) {
                // Chuyển sang QuizActivity nếu có
                // startActivity(new Intent(this, QuizActivity.class));
                Toast.makeText(this, "Tính năng Quiz sẽ được phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Chuyển sang Profile
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void loadEvents() {
        eventManager.getAllEvents(new EventManager.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserHomeActivity.this, "Lỗi tải sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }
} 