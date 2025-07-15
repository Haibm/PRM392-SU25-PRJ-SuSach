package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.susach.R;
import com.google.firebase.auth.FirebaseAuth;

public class StaffActivity extends AppCompatActivity {

    private Button btnManageArticle;
    private Button btnManageQuiz;
    private Button btnLogout;
    private String staffEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnManageArticle = findViewById(R.id.btn_manage_article);
        btnManageQuiz = findViewById(R.id.btn_manage_quiz);
        btnLogout = findViewById(R.id.btn_logout);

        // Lấy email staff từ Intent
        staffEmail = getIntent().getStringExtra("staff_email");

        btnManageArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffActivity.this, ManageArticleActivity.class);
                intent.putExtra("staff_email", staffEmail);
                startActivity(intent);
            }
        });

        btnManageQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffActivity.this, QuizSetListActivity.class);
                intent.putExtra("staff_email", staffEmail);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(StaffActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}