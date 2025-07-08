package com.example.susach;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rcvLeaderboard;
    TextView tvMyGrade, tvMyGrade10;
    private List<Leaderboard> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);

        BindViews();
        RetrieveData();
        setupRecyclerView();
    }
    private void BindViews(){
        tvMyGrade = findViewById(R.id.tvMyGrade);
        tvMyGrade10 = findViewById(R.id.tvMyGrade10);
        rcvLeaderboard = findViewById(R.id.rcvLeaderboard);
        rcvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
    }

    private LeaderboardAdapter adapter;
    private void setupRecyclerView(){
        Collections.sort(userList, (o1, o2) -> o2.getGrade() - o1.getGrade());
        adapter = new LeaderboardAdapter(userList);
        rcvLeaderboard.setAdapter(adapter);

    }

    private void RetrieveData() {

        db.collection("leaderboard")
                .document("quiz1")
                .collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            int grade = document.getLong("grade").intValue();
                            float grade10 = document.getDouble("grade10").floatValue();

                            Leaderboard leaderboard = new Leaderboard(name, grade, grade10);
                            userList.add(leaderboard);
                        }
                        Collections.sort(userList, (o1, o2) -> o2.getGrade() - o1.getGrade());
                        adapter.notifyDataSetChanged(); // Notify the adapter here

                    } else {
                        Log.w("FIRESTORE", "Error getting users", task.getException());
                    }
                });

        float grade10 = getIntent().getFloatExtra("grade10", 0);
        int grade = getIntent().getIntExtra("grade", 0);
        tvMyGrade10.setText(String.valueOf(grade10));
        tvMyGrade.setText(String.valueOf(grade));
    }
}