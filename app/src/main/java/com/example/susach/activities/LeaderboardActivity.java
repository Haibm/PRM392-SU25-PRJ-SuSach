package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import com.example.susach.models.Leaderboard;
import com.example.susach.adapters.LeaderboardAdapter;
import com.example.susach.managers.SoundManager;
import com.example.susach.firebase.QuizData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";
    private RecyclerView rcvLeaderboard;
    private TextView tvMyGrade, tvMyGrade10;
    private LeaderboardAdapter adapter;
    private List<Leaderboard> userList = new ArrayList<>();
    private QuizData quizData;
    private SoundManager soundManager;
    private String quizSetName;
    private Button btnReturn;

    private void bindingView() {
        tvMyGrade = findViewById(R.id.tvMyGrade);
        tvMyGrade10 = findViewById(R.id.tvMyGrade10);
        rcvLeaderboard = findViewById(R.id.rcvLeaderboard);
        rcvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 10);
        adapter = new LeaderboardAdapter(userList, totalQuestions);
        rcvLeaderboard.setAdapter(adapter);
        btnReturn = findViewById(R.id.btnReturn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindingView();
        float grade10 = getIntent().getFloatExtra("grade10", 0);
        int grade = getIntent().getIntExtra("grade", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 10);
        String formattedGrade10 = String.format("%.1f", grade10);
        tvMyGrade10.setText(formattedGrade10);
        tvMyGrade.setText(grade + "/" + totalQuestions);
        quizSetName = getIntent().getStringExtra("quizSetName");
        if (quizSetName == null) quizSetName = "quiz1";
        quizData = new QuizData();
        soundManager = new SoundManager(this);
        
        // Set quiz set name
        TextView tvQuizSetName = findViewById(R.id.tvQuizSetName);
        loadQuizSetDisplayName(quizSetName, tvQuizSetName);
        
        loadLeaderboardData();
        
        // Play finish sound when leaderboard is displayed
        if (soundManager != null) {
            soundManager.playFinishSound();
        }
        
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(LeaderboardActivity.this, QuizSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tự động refresh leaderboard khi quay lại màn hình
        loadLeaderboardData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LeaderboardActivity.this, QuizSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void loadLeaderboardData() {
        quizData.getLeaderboardList(quizSetName, new QuizData.LeaderboardDataCallback() {
            @Override
            public void onDataLoaded(List<Leaderboard> list) {
                userList.clear();
                userList.addAll(list);
                adapter.notifyDataSetChanged();
                
                // Log thông tin Top 15
                Log.d(TAG, "Loaded Top " + list.size() + " users for quizSetName: " + quizSetName);
                for (int i = 0; i < list.size(); i++) {
                    Leaderboard user = list.get(i);
                    Log.d(TAG, "Rank " + (i + 1) + ": " + user.getName() + 
                          " - Score: " + user.getGrade10() + 
                          " - Time: " + user.getTotalTime() + "s");
                }
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(LeaderboardActivity.this, "Lỗi tải dữ liệu bảng xếp hạng", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading leaderboard", e);
            }
        });
    }

    private void saveLeaderboardData() {

    }

    /**
     * Lấy tên hiển thị của bộ quiz từ Firebase
     */
    private void loadQuizSetDisplayName(String quizSetName, TextView textView) {
        Log.d(TAG, "Loading display name for quizSetName: " + quizSetName);
        
        // Thử collection "quizs" trước
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("quizs")
            .document(quizSetName)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Log.d(TAG, "Document exists in 'quizs': " + documentSnapshot.exists());
                if (documentSnapshot.exists()) {
                    // Thử các field name khác nhau
                    String displayName = documentSnapshot.getString("displayName");
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = documentSnapshot.getString("name");
                    }
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = documentSnapshot.getString("title");
                    }
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = documentSnapshot.getString("quizSetName");
                    }
                    
                    Log.d(TAG, "Display name from Firebase: " + displayName);
                    if (displayName != null && !displayName.isEmpty()) {
                        textView.setText(displayName);
                        Log.d(TAG, "Set display name to: " + displayName);
                    } else {
                        // Fallback nếu không có displayName
                        String fallbackName = quizSetName;
                        textView.setText(fallbackName);
                        Log.d(TAG, "Set fallback name to: " + fallbackName);
                    }
                } else {
                    // Thử collection "quizSets" nếu không tìm thấy trong "quizs"
                    tryAlternativeCollection(quizSetName, textView);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading from 'quizs' collection", e);
                // Thử collection khác nếu có lỗi
                tryAlternativeCollection(quizSetName, textView);
            });
    }
    
    private void tryAlternativeCollection(String quizSetName, TextView textView) {
        Log.d(TAG, "Trying alternative collection for: " + quizSetName);
        
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("quizSets")
            .document(quizSetName)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Log.d(TAG, "Document exists in 'quizSets': " + documentSnapshot.exists());
                if (documentSnapshot.exists()) {
                    String displayName = documentSnapshot.getString("displayName");
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = documentSnapshot.getString("name");
                    }
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = documentSnapshot.getString("title");
                    }
                    
                    if (displayName != null && !displayName.isEmpty()) {
                        textView.setText(displayName);
                        Log.d(TAG, "Set display name from alternative collection: " + displayName);
                    } else {
                        String fallbackName = "Bộ Quiz 1" + quizSetName;
                        textView.setText(fallbackName);
                        Log.d(TAG, "Set fallback name from alternative collection: " + fallbackName);
                    }
                } else {
                    // Final fallback
                    String fallbackName = "Bộ Quiz 2" + quizSetName;
                    textView.setText(fallbackName);
                    Log.d(TAG, "Document doesn't exist in any collection, set fallback name to: " + fallbackName);
                }
            })
            .addOnFailureListener(e -> {
                // Final fallback nếu có lỗi
                String fallbackName = "Bộ Quiz 3" + quizSetName;
                textView.setText(fallbackName);
                Log.e(TAG, "Error loading from alternative collection", e);
            });
    }

    @Override
    protected void onDestroy() {
        if (soundManager != null) {
            soundManager.release();
        }
        super.onDestroy();
    }

    interface LeaderboardDataCallback {
        void onDataLoaded(List<Leaderboard> list);
        void onError(Exception e);
    }
}