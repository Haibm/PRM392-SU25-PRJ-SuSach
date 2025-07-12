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
    private String quizSetName;
    private Button btnReturn;

    private void bindingView() {
        tvMyGrade = findViewById(R.id.tvMyGrade);
        tvMyGrade10 = findViewById(R.id.tvMyGrade10);
        rcvLeaderboard = findViewById(R.id.rcvLeaderboard);
        rcvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter(userList);
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
        String formattedGrade10 = String.format("%.2f", grade10);
        tvMyGrade10.setText(String.valueOf(formattedGrade10));
        tvMyGrade.setText(String.valueOf(grade));
        quizSetName = getIntent().getStringExtra("quizSetName");
        if (quizSetName == null) quizSetName = "quiz1";
        quizData = new QuizData();
        loadLeaderboardData();
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(LeaderboardActivity.this, QuizSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
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

    interface LeaderboardDataCallback {
        void onDataLoaded(List<Leaderboard> list);
        void onError(Exception e);
    }
}