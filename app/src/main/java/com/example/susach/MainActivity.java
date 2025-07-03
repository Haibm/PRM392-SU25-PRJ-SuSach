package com.example.susach;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerEraId;
    private EditText etName, etDescription, etContents, etStartDate, etEndDate, etImageUrl;
    private Button btnCreate;
    private TextView tvStatus;
    private ArrayAdapter<String> eraAdapter;
    private List<String> eraNameList = new ArrayList<>();
    private List<String> eraIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerEraId = findViewById(R.id.spinnerEraId);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etContents = findViewById(R.id.etContents);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnCreate = findViewById(R.id.btnCreate);
        tvStatus = findViewById(R.id.tvStatus);

        eraAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eraNameList);
        eraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEraId.setAdapter(eraAdapter);

        loadEraIds();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void loadEraIds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("eras").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eraIdList.clear();
            eraNameList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                eraIdList.add(doc.getId());
                String name = doc.contains("name") ? doc.getString("name") : doc.getId();
                eraNameList.add(name);
            }
            eraAdapter.notifyDataSetChanged();
        });
    }

    private void createEvent() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String contents = etContents.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        int eraIndex = spinnerEraId.getSelectedItemPosition();
        String eraId = (eraIndex >= 0 && eraIndex < eraIdList.size()) ? eraIdList.get(eraIndex) : null;

        if (name.isEmpty() || description.isEmpty() || contents.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || eraId == null) {
            tvStatus.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        int startDate, endDate;
        try {
            startDate = Integer.parseInt(startDateStr);
            endDate = Integer.parseInt(endDateStr);
        } catch (NumberFormatException e) {
            tvStatus.setText("Năm bắt đầu/kết thúc phải là số!");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        // Lấy id tự tăng
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int maxId = 0;
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                try {
                    int id = Integer.parseInt(doc.getString("id"));
                    if (id > maxId) maxId = id;
                } catch (Exception ignored) {}
            }
            int newId = maxId + 1;
            Map<String, Object> event = new HashMap<>();
            event.put("id", String.valueOf(newId));
            event.put("name", name);
            event.put("description", description);
            event.put("contents", contents);
            event.put("startDate", startDate);
            event.put("endDate", endDate);
            event.put("imageUrl", imageUrl);
            event.put("eraId", eraId);
            eventsRef.add(event)
                .addOnSuccessListener(documentReference -> tvStatus.setText("Tạo sự kiện thành công!"))
                .addOnFailureListener(e -> tvStatus.setText("Lỗi: " + e.getMessage()));
        });
    }
}