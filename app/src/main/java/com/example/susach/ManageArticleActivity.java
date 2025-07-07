package com.example.susach;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.susach.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageArticleActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText etId, etName, etDescription, etContents, etStartDate, etEndDate, etImageUrl, etSummary, etImageContent;
    private Spinner spinnerEraId;
    private Button btnAdd, btnUpdate, btnDelete, btnBack;
    private ListView listViewArticles;
    private ArrayAdapter<String> adapter;
    private List<String> articleList;
    private List<String> articleIdList;
    private String selectedArticleId = "";
    private String staffEmail = "";
    private List<String> eraIdList = new ArrayList<>();
    private List<String> eraNameList = new ArrayList<>();
    private ArrayAdapter<String> eraAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_article);

        db = FirebaseFirestore.getInstance();
        staffEmail = getIntent().getStringExtra("staff_email");

        initializeViews();
        loadEraIds();
        loadArticles();
        setupClickListeners();
    }

    private void initializeViews() {
        etId = findViewById(R.id.et_id);
        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        etContents = findViewById(R.id.et_contents);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etImageUrl = findViewById(R.id.et_image_url);
        etSummary = findViewById(R.id.et_summary);
        etImageContent = findViewById(R.id.et_image_content);
        spinnerEraId = findViewById(R.id.spinner_era_id);
        btnAdd = findViewById(R.id.btn_add);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
        listViewArticles = findViewById(R.id.listview_articles);

        articleList = new ArrayList<>();
        articleIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, articleList);
        listViewArticles.setAdapter(adapter);

        eraAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eraNameList);
        eraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEraId.setAdapter(eraAdapter);
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArticle();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArticle();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArticle();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedArticleId = articleIdList.get(position);
                loadArticleDetails(selectedArticleId);
            }
        });
    }

    private void loadEraIds() {
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

    private void loadArticles() {
        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            articleList.clear();
                            articleIdList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    articleList.add(event.getId() + " - " + event.getName() + " (by: " + (event.getUpdateBy() != null ? event.getUpdateBy() : "") + ")");
                                    articleIdList.add(event.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ManageArticleActivity.this, "Error loading articles", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadArticleDetails(String articleId) {
        db.collection("events").document(articleId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Event event = task.getResult().toObject(Event.class);
                            if (event != null) {
                                etId.setText(event.getId());
                                etName.setText(event.getName());
                                etDescription.setText(event.getDescription());
                                etContents.setText(event.getContents());
                                etStartDate.setText(String.valueOf(event.getStartDate()));
                                etEndDate.setText(String.valueOf(event.getEndDate()));
                                etImageUrl.setText(event.getImageUrl());
                                etSummary.setText(event.getSummary());
                                etImageContent.setText(event.getImageContent());
                                String eraId = event.getEraId();
                                int eraIndex = eraIdList.indexOf(eraId);
                                if (eraIndex >= 0) spinnerEraId.setSelection(eraIndex);
                            }
                        }
                    }
                });
    }

    private void addArticle() {
        String id = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String contents = etContents.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String summary = etSummary.getText().toString().trim();
        String imageContent = etImageContent.getText().toString().trim();
        int eraIndex = spinnerEraId.getSelectedItemPosition();
        String eraId = (eraIndex >= 0 && eraIndex < eraIdList.size()) ? eraIdList.get(eraIndex) : null;

        if (id.isEmpty() || name.isEmpty() || description.isEmpty() || contents.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || eraId == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int startDate, endDate;
        try {
            startDate = Integer.parseInt(startDateStr);
            endDate = Integer.parseInt(endDateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm bắt đầu/kết thúc phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(id, name, description, contents, startDate, endDate, imageUrl, eraId, summary, imageContent, staffEmail, staffEmail);
        db.collection("events").document(id).set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ManageArticleActivity.this, "Thêm bài viết thành công!", Toast.LENGTH_SHORT).show();
                        loadArticles();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateArticle() {
        String id = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String contents = etContents.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String summary = etSummary.getText().toString().trim();
        String imageContent = etImageContent.getText().toString().trim();
        int eraIndex = spinnerEraId.getSelectedItemPosition();
        String eraId = (eraIndex >= 0 && eraIndex < eraIdList.size()) ? eraIdList.get(eraIndex) : null;

        if (id.isEmpty() || name.isEmpty() || description.isEmpty() || contents.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || eraId == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int startDate, endDate;
        try {
            startDate = Integer.parseInt(startDateStr);
            endDate = Integer.parseInt(endDateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm bắt đầu/kết thúc phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(id).get().addOnSuccessListener(document -> {
            Event oldEvent = document.toObject(Event.class);
            String createBy = (oldEvent != null && oldEvent.getCreateBy() != null) ? oldEvent.getCreateBy() : staffEmail;
            Event event = new Event(id, name, description, contents, startDate, endDate, imageUrl, eraId, summary, imageContent, createBy, staffEmail);
            db.collection("events").document(id).set(event)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ManageArticleActivity.this, "Cập nhật bài viết thành công!", Toast.LENGTH_SHORT).show();
                            loadArticles();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void deleteArticle() {
        String id = etId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Chọn bài viết để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("events").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ManageArticleActivity.this, "Xóa bài viết thành công!", Toast.LENGTH_SHORT).show();
                        loadArticles();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}