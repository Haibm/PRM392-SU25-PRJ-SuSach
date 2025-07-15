package com.example.susach.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.susach.adapters.ArticleAdapter;
import com.example.susach.databinding.ActivityManageArticleBinding;
import com.example.susach.managers.EventManager;
import com.example.susach.models.Event;
import com.example.susach.dialogs.ArticleEditDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;

public class ManageArticleActivity extends AppCompatActivity {
    private ActivityManageArticleBinding binding;
    private ArticleAdapter articleAdapter;
    private List<Event> articleList = new ArrayList<>();
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup RecyclerView
        articleAdapter = new ArticleAdapter(articleList, new ArticleAdapter.OnActionListener() {
            @Override
            public void onUpdate(Event event) {
                showEditDialog(event);
            }
            @Override
            public void onDelete(Event event) {
                confirmDelete(event);
            }
        });
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerEvents.setAdapter(articleAdapter);

        eventManager = new EventManager();
        loadArticles();

        // Nút thêm mới
        binding.fabAddArticle.setOnClickListener(v -> showEditDialog(null));
    }

    private void loadArticles() {
        eventManager.getAllEvents(new EventManager.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                articleList.clear();
                articleList.addAll(events);
                articleAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageArticleActivity.this, "Lỗi tải bài viết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Event event) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa bài viết")
            .setMessage("Bạn có chắc muốn xóa bài viết này?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                eventManager.deleteEvent(event.getId(), new EventManager.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ManageArticleActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        loadArticles();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void showEditDialog(Event event) {
        ArticleEditDialog dialog = new ArticleEditDialog(event, new ArticleEditDialog.OnSaveListener() {
            @Override
            public void onSave(Event newEvent) {
                boolean isAdd = (event == null);
                eventManager.saveEvent(newEvent, isAdd, new EventManager.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ManageArticleActivity.this, (isAdd ? "Thêm" : "Cập nhật") + " bài viết thành công!", Toast.LENGTH_SHORT).show();
                        loadArticles();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ManageArticleActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), "ArticleEditDialog");
    }
}