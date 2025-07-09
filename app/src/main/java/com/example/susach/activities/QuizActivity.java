package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.susach.R;
import java.util.List;
import com.example.susach.models.Quiz;
import com.example.susach.managers.QuizManager;
import com.example.susach.firebase.QuizData;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private QuizManager quizManager;
    private QuizData quizData;
    private String quizSetName;

    private void bindingView() {
        tvQuestion = findViewById(R.id.tvQuestion);
        btnAnswer1 = findViewById(R.id.btnAnswer1);
        btnAnswer2 = findViewById(R.id.btnAnswer2);
        btnAnswer3 = findViewById(R.id.btnAnswer3);
        btnAnswer4 = findViewById(R.id.btnAnswer4);
    }

    private void bindingAction() {
        btnAnswer1.setOnClickListener(this::onAnswerClick);
        btnAnswer2.setOnClickListener(this::onAnswerClick);
        btnAnswer3.setOnClickListener(this::onAnswerClick);
        btnAnswer4.setOnClickListener(this::onAnswerClick);
    }


    private void showQuestion() {
        if (quizManager != null && quizManager.hasCurrentQuiz()) {
            Quiz quiz = quizManager.getCurrentQuiz();
            tvQuestion.setText(quiz.getQuestion());
            btnAnswer1.setText(quiz.getAnswer1());
            btnAnswer2.setText(quiz.getAnswer2());
            btnAnswer3.setText(quiz.getAnswer3());
            btnAnswer4.setText(quiz.getAnswer4());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindingView();
        bindingAction();
        quizSetName = getIntent().getStringExtra("quizSetName");
        quizData = new QuizData();
        loadQuizData();
    }

    private void loadQuizData() {
        quizData.getQuizList(quizSetName,new QuizData.QuizDataCallback() {
            @Override
            public void onDataLoaded(List<Quiz> quizList) {
                quizManager = new QuizManager(quizList);
                showQuestion();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(QuizActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onAnswerClick(View view) {
        int chosen = 0;
        if (view.getId() == R.id.btnAnswer1) chosen = 1;
        else if (view.getId() == R.id.btnAnswer2) chosen = 2;
        else if (view.getId() == R.id.btnAnswer3) chosen = 3;
        else if (view.getId() == R.id.btnAnswer4) chosen = 4;

        if (quizManager != null) {
            quizManager.checkAnswer(chosen);
            if (quizManager.nextQuiz()) {
                showQuestion();
            } else {
                Toast.makeText(this, "Grade: " + quizManager.getScore(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LeaderboardActivity.class);
                intent.putExtra("grade", quizManager.getScore());
                intent.putExtra("grade10", quizManager.getScore10());
                intent.putExtra("quizSetName", quizSetName);
                startActivity(intent);
            }
        }
    }
}



