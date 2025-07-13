package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import com.example.susach.adapters.EventAdapter;
import com.example.susach.models.Event;
import com.example.susach.models.Era;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExploreActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Spinner spinnerEra;
    private Button btnRandomEvent;
    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private List<Era> eraList = new ArrayList<>();
    private ArrayAdapter<String> eraAdapter;
    private String selectedEraId = null;
    private Button btnSearch;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        searchEditText = findViewById(R.id.searchEditText);
        spinnerEra = findViewById(R.id.spinnerEra);
        btnRandomEvent = findViewById(R.id.btnRandomEvent);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        btnSearch = findViewById(R.id.btnSearch);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        eventAdapter = new EventAdapter(filteredEvents, event -> openEventDetail(event));
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(eventAdapter);

        loadEras();
        loadEvents();

        spinnerEra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedEraId = null;
                    Log.d("ExploreActivity", "Selected Era: All Eras (null)");
                } else {
                    selectedEraId = eraList.get(position - 1).getId();
                    Log.d("ExploreActivity", "Selected Era ID: " + selectedEraId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEraId = null;
                Log.d("ExploreActivity", "No Era Selected (null)");
            }
        });

        btnSearch.setOnClickListener(v -> filterEvents());

        ////Dat: Xử lý BottomNavigationView
        setupBottomNavigation();
        bottomNavigation.setSelectedItemId(R.id.navigation_explore);

        btnRandomEvent.setOnClickListener(v -> {
            if (filteredEvents.isEmpty()) {
                Toast.makeText(this, "Không có sự kiện nào!", Toast.LENGTH_SHORT).show();
                return;
            }
            Event randomEvent = filteredEvents.get(new Random().nextInt(filteredEvents.size()));
            new AlertDialog.Builder(this)
                    .setTitle("Khám phá ngẫu nhiên")
                    .setMessage("Bạn muốn xem sự kiện: " + randomEvent.getName() + "?")
                    .setPositiveButton("Xem", (dialog, which) -> openEventDetail(randomEvent))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void loadEras() {
        FirebaseFirestore.getInstance().collection("eras").get().addOnSuccessListener(query -> {
            eraList.clear();
            List<Era> tempEraList = new ArrayList<>();
            List<String> eraNames = new ArrayList<>();
            eraNames.add("Tất cả thời kỳ");
            for (DocumentSnapshot doc : query) {
                Era era = doc.toObject(Era.class);
                if (era != null) {
                    era.setId(doc.getId()); // Gán document ID vào id của Era
                    if (era.getId() != null && era.getName() != null) { // Kiểm tra id và name không null
                        tempEraList.add(era);
                        Log.d("ExploreActivity", "Loaded Era: id=" + era.getId() + ", name=" + era.getName());
                    } else {
                        Log.w("ExploreActivity", "Ignored Era with null id or name: " + doc.getId());
                    }
                }
            }
            // Sắp xếp tempEraList theo id
            tempEraList.sort((a, b) -> {
                if (a.getId() == null || b.getId() == null) {
                    Log.w("ExploreActivity", "Null id detected during sort, skipping sort for safety");
                    return 0; // Tránh lỗi, giữ nguyên thứ tự
                }
                return a.getId().compareTo(b.getId());
            });
            eraList.addAll(tempEraList);
            for (Era era : eraList) {
                eraNames.add(era.getName());
            }
            eraAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eraNames);
            eraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEra.setAdapter(eraAdapter);
        }).addOnFailureListener(e -> {
            Log.e("ExploreActivity", "Error loading eras: " + e.getMessage());
            Toast.makeText(this, "Lỗi tải thời kỳ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadEvents() {
        FirebaseFirestore.getInstance().collection("events").get().addOnSuccessListener(query -> {
            allEvents.clear();
            for (DocumentSnapshot doc : query) {
                Event event = doc.toObject(Event.class);
                if (event != null) {
                    allEvents.add(event);
                    Log.d("ExploreActivity", "Loaded Event: " + event.getName() + ", EraId: " + event.getEraId());
                }
            }
            Log.d("ExploreActivity", "Total Events Loaded: " + allEvents.size());
            filterEvents();
        }).addOnFailureListener(e -> {
            Log.e("ExploreActivity", "Error loading events: " + e.getMessage());
            Toast.makeText(this, "Lỗi tải sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void filterEvents() {
        if (allEvents.isEmpty()) {
            Toast.makeText(this, "Danh sách sự kiện trống. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            return;
        }
        String keyword = searchEditText.getText().toString().trim().toLowerCase();
        filteredEvents.clear();
        String eraIdToFilter = selectedEraId;

        for (Event event : allEvents) {
            boolean matchEra = (eraIdToFilter == null ||
                    (event.getEraId() != null && event.getEraId().equals(eraIdToFilter)));
            boolean matchKeyword = keyword.isEmpty() ||
                    (event.getName() != null && event.getName().toLowerCase().contains(keyword));

            if (matchEra && matchKeyword) {
                filteredEvents.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
        if (filteredEvents.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sự kiện nào!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

    ////Dat: Method xử lý BottomNavigationView (có thể tái sử dụng)
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, UserHomeActivity.class));
                return true;
            } else if (itemId == R.id.navigation_explore) {
                return true;
            } else if (itemId == R.id.navigation_quiz) {
                ////Dat: Chuyển sang QuizSelectActivity
                Intent intent = new Intent(this, QuizSelectActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}