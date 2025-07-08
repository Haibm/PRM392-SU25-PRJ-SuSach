package com.example.susach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    TextView tvQuestion;
    Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Quiz> quizList = new ArrayList<>();
    private short grade = 0;
    private int currentQuestionIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        BindViews();
        RetrieveData();
        setupAnswerButtons();
    }

    private void BindViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        btnAnswer1 = findViewById(R.id.btnAnswer1);
        btnAnswer2 = findViewById(R.id.btnAnswer2);
        btnAnswer3 = findViewById(R.id.btnAnswer3);
        btnAnswer4 = findViewById(R.id.btnAnswer4);
    }

    private void RetrieveData() {

        db.collection("quizs")
                .document("quiz1")
                .collection("quiz1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        quizList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String question = document.getString("question");
                            String answer1 = document.getString("answer1");
                            String answer2 = document.getString("answer2");
                            String answer3 = document.getString("answer3");
                            String answer4 = document.getString("answer4");
                            int correct = document.getLong("correct").intValue();

                            Quiz quiz = new Quiz(question, answer1, answer2, answer3, answer4, correct);
                            quizList.add(quiz);
                        }

                        if (!quizList.isEmpty()) {
                            currentQuestionIndex = 0;
                            showQuestion(currentQuestionIndex);
                        }
                    } else {
                        Log.w("FIRESTORE", "Error getting questions", task.getException());
                    }
                });
    }

    private void showQuestion(int index) {
        if (index < quizList.size()) {
            Quiz quiz = quizList.get(index);
            tvQuestion.setText(quiz.getQuestion());
            btnAnswer1.setText(quiz.getAnswer1());
            btnAnswer2.setText(quiz.getAnswer2());
            btnAnswer3.setText(quiz.getAnswer3());
            btnAnswer4.setText(quiz.getAnswer4());
        } else {
            Toast.makeText(this, "Grade: "+grade, Toast.LENGTH_SHORT).show();
            // Navigate to result or restart quiz

            Intent intent = new Intent(this, LeaderboardActivity.class);
            intent.putExtra("grade", grade);
            float grade10 = ((float)grade / quizList.size()) * 10;
            intent.putExtra("grade10", grade10);
            startActivity(intent);
        }
    }

    private int chosen;
    private void setupAnswerButtons() {
        View.OnClickListener answerClickListener = v -> {
            int id = v.getId();
            if (id == R.id.btnAnswer1) {
                chosen = 1;
            } else if (id == R.id.btnAnswer2) {
                chosen = 2;
            } else if (id == R.id.btnAnswer3) {
                chosen = 3;
            } else if (id == R.id.btnAnswer4) {
                chosen = 4;
            } else {
                chosen = 0;
            }

            Quiz quiz = quizList.get(currentQuestionIndex);
            if (chosen == quiz.getCorrect()){
                grade++;
            }
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        };

        btnAnswer1.setOnClickListener(answerClickListener);
        btnAnswer2.setOnClickListener(answerClickListener);
        btnAnswer3.setOnClickListener(answerClickListener);
        btnAnswer4.setOnClickListener(answerClickListener);
    }

}
