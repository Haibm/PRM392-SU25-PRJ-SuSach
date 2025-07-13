package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.susach.R;
import com.example.susach.adapters.QuizSelectAdapter;
import com.example.susach.adapters.QuizSetAdapter;
import com.example.susach.firebase.QuizData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class QuizSelectActivity extends AppCompatActivity {

    RecyclerView rcvQuizSetList;;
    QuizData quizData = new QuizData();
    private QuizSelectAdapter adapter;
    private List<String> quizSetList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_select);

        ////Dat: Xử lý BottomNavigationView
        setupBottomNavigation();

        bindingView();
        loadQuizSetList();
    }

    private void bindingView(){
        rcvQuizSetList = findViewById(R.id.rcvQuizSetList);
        adapter = new QuizSelectAdapter(quizSetList, new QuizSelectAdapter.QuizSelectListener() {
            @Override
            public void onQuizSelectClick(String quizSetName) {
                // Di toi bo quiz
                Intent intent = new Intent(QuizSelectActivity.this, QuizActivity.class);
                intent.putExtra("quizSetName", quizSetName);
                startActivity(intent);
            }


        });
        rcvQuizSetList.setLayoutManager(new LinearLayoutManager(this));
        rcvQuizSetList.setAdapter(adapter);
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
                Toast.makeText(QuizSelectActivity.this, "Lỗi tải danh sách bộ quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ////Dat: Method xử lý BottomNavigationView (có thể tái sử dụng)
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_quiz);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, UserHomeActivity.class));
                return true;
            } else if (itemId == R.id.navigation_explore) {
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            } else if (itemId == R.id.navigation_quiz) {
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}