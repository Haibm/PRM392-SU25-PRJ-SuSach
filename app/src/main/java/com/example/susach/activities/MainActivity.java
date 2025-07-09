package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.susach.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Nếu muốn chuyển hướng sang danh sách bộ câu hỏi:
        Intent intent = new Intent(this, QuizSetListActivity.class);
        startActivity(intent);
        // Nếu chỉ muốn hiển thị layout chính thì xóa 2 dòng trên
    }
}