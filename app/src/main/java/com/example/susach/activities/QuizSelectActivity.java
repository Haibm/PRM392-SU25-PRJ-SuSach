package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.susach.R;

public class QuizSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_select);

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(view -> {
            Intent intent = new Intent(QuizSelectActivity.this, QuizActivity.class);
            intent.putExtra("quizSetName", "quiz2");
            startActivity(intent);
        });

    }
}