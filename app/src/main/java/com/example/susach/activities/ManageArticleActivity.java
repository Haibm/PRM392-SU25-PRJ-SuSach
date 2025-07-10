package com.example.susach.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.susach.adapters.EventAdapter;
import com.example.susach.databinding.ActivityManageArticleBinding;
import com.example.susach.managers.EventManager;
import com.example.susach.models.Event;
import java.util.ArrayList;
import java.util.List;

public class ManageArticleActivity extends AppCompatActivity {
    private ActivityManageArticleBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventAdapter = new EventAdapter(eventList, event -> LoadEventToForm(event));
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerEvents.setAdapter(eventAdapter);

        eventManager = new EventManager();
        loadEvents();

        binding.btnAdd.setOnClickListener(v -> addOrUpdateEvent(true));
        binding.btnUpdate.setOnClickListener(v -> addOrUpdateEvent(false));
        binding.btnDelete.setOnClickListener(v -> deleteEvent());
        binding.btnBack.setOnClickListener(v -> finish());
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
                Toast.makeText(ManageArticleActivity.this, "Lỗi tải sự kiện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadEventToForm(Event event) {
        binding.etId.setText(event.getId());
        binding.etName.setText(event.getName());
        binding.etDescription.setText(event.getDescription());
        binding.etContents.setText(event.getContents());
        binding.etStartDate.setText(String.valueOf(event.getStartDate()));
        binding.etEndDate.setText(String.valueOf(event.getEndDate()));
        binding.etImageUrl.setText(event.getImageUrl());
        binding.etSummary.setText(event.getSummary());
        binding.etImageContent.setText(event.getImageContent());
        // TODO: set era spinner selection
    }

    private void addOrUpdateEvent(boolean isAdd) {
        String id = binding.etId.getText().toString().trim();
        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String contents = binding.etContents.getText().toString().trim();
        String startDateStr = binding.etStartDate.getText().toString().trim();
        String endDateStr = binding.etEndDate.getText().toString().trim();
        String imageUrl = binding.etImageUrl.getText().toString().trim();
        String summary = binding.etSummary.getText().toString().trim();
        String imageContent = binding.etImageContent.getText().toString().trim();
        // TODO: get eraId from spinner
        String eraId = "";
        int startDate, endDate;
        try {
            startDate = Integer.parseInt(startDateStr);
            endDate = Integer.parseInt(endDateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm bắt đầu/kết thúc phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }
        Event event = new Event(id, name, description, contents, startDate, endDate, imageUrl, eraId, summary, imageContent, "staff@example.com", "staff@example.com");
        eventManager.saveEvent(event, isAdd, new EventManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ManageArticleActivity.this, (isAdd ? "Thêm" : "Cập nhật") + " bài viết thành công!", Toast.LENGTH_SHORT).show();
                loadEvents();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEvent() {
        String id = binding.etId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Chọn bài viết để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }
        eventManager.deleteEvent(id, new EventManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ManageArticleActivity.this, "Xóa bài viết thành công!", Toast.LENGTH_SHORT).show();
                loadEvents();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}