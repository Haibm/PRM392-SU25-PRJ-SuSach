package com.example.susach.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.adapters.QuizSetAdapter;
import com.example.susach.firebase.QuizData;
import com.example.susach.R;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class QuizSetListActivity extends AppCompatActivity {
    private RecyclerView rcvQuizSet;
    private QuizSetAdapter adapter;
    private List<String> quizSetList = new ArrayList<>();
    private QuizData quizData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_set_list);
        bindingView();
        bindingAction();
        loadQuizSetList();
    }

    private void bindingView() {
        rcvQuizSet = findViewById(R.id.rcvQuizSet);
        quizData = new QuizData();
        adapter = new QuizSetAdapter(quizSetList, new QuizSetAdapter.QuizSetListener() {
            @Override
            public void onQuizSetClick(String quizSetName) {
                // Mở màn hình danh sách quiz của bộ này
                Intent intent = new Intent(QuizSetListActivity.this, QuizListActivity.class);
                intent.putExtra("quizSetName", quizSetName);
                startActivity(intent);
            }
            @Override
            public void onEditQuizSet(String quizSetName) {
                showEditQuizSetDialog(quizSetName);
            }
            @Override
            public void onDeleteQuizSet(String quizSetName) {
                showDeleteQuizSetDialog(quizSetName);
            }
        });
        rcvQuizSet.setLayoutManager(new LinearLayoutManager(this));
        rcvQuizSet.setAdapter(adapter);
    }

    private void bindingAction() {
        findViewById(R.id.btnAddQuizSet).setOnClickListener(this::onBtnAddQuizSetClick);
    }

    private void onBtnAddQuizSetClick(View v) {
        showAddQuizSetDialog();
    }

    private void loadQuizSetList() {
        quizData.getQuizSetList(new QuizData.QuizSetCallback() {
            @Override
            public void onDataLoaded(List<String> quizSetNames) {
                quizSetList.clear();
                quizSetList.addAll(quizSetNames);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(QuizSetListActivity.this, "Lỗi tải danh sách bộ quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddQuizSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm bộ quiz mới");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String quizSetName = input.getText().toString().trim();
            if (!quizSetName.isEmpty()) {
                quizData.addQuizSet(quizSetName, task -> {
                    if (task.isSuccessful()) {
                        loadQuizSetList();
                        Toast.makeText(this, "Đã thêm bộ quiz!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteQuizSetDialog(String quizSetName) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa bộ quiz")
            .setMessage("Bạn có chắc muốn xóa bộ quiz này không?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                quizData.deleteQuizSet(quizSetName, task -> {
                    if (task.isSuccessful()) {
                        loadQuizSetList();
                        Toast.makeText(this, "Đã xóa bộ quiz!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void showEditQuizSetDialog(String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi tên bộ quiz");
        final EditText input = new EditText(this);
        input.setText(oldName);
        builder.setView(input);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(oldName)) {
                quizData.renameQuizSet(oldName, newName, task -> {
                    if (task.isSuccessful()) {
                        loadQuizSetList();
                        Toast.makeText(this, "Đã đổi tên!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Lỗi khi đổi tên!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
} 