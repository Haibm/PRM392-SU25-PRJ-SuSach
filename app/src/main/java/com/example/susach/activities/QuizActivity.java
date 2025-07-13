package com.example.susach.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.susach.R;
import java.util.List;
import com.example.susach.models.Quiz;
import com.example.susach.managers.QuizManager;
import com.example.susach.managers.SoundManager;
import com.example.susach.firebase.QuizData;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvProgressText, tvScore, tvTimer;
    private TextView tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4;
    private CardView cardAnswer1, cardAnswer2, cardAnswer3, cardAnswer4;
    private LinearProgressIndicator progressBar;
    private QuizManager quizManager;
    private QuizData quizData;
    private SoundManager soundManager;
    private String quizSetName;
    private String currentUserName;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 0;
    private boolean isAnswered = false;
    private int selectedAnswer = -1;
    private long startTime; // Thời gian bắt đầu làm bài
    private long questionStartTime; // Thời gian bắt đầu câu hỏi hiện tại
    private int totalTimeSeconds = 0; // Tổng thời gian làm bài (giây)
    
    // Animation constants
    private static final int ANIMATION_DURATION = 300;
    private static final int ANSWER_REVEAL_DURATION = 600;

    private CountDownTimer countDownTimer;
    private static final long QUESTION_TIME_MILLIS = 30000; // 30s

    private void bindingView() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgressText = findViewById(R.id.tvProgressText);
        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        
        tvAnswer1 = findViewById(R.id.tvAnswer1);
        tvAnswer2 = findViewById(R.id.tvAnswer2);
        tvAnswer3 = findViewById(R.id.tvAnswer3);
        tvAnswer4 = findViewById(R.id.tvAnswer4);
        
        cardAnswer1 = findViewById(R.id.cardAnswer1);
        cardAnswer2 = findViewById(R.id.cardAnswer2);
        cardAnswer3 = findViewById(R.id.cardAnswer3);
        cardAnswer4 = findViewById(R.id.cardAnswer4);
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void bindingAction() {
        cardAnswer1.setOnClickListener(this::onAnswerClick);
        cardAnswer2.setOnClickListener(this::onAnswerClick);
        cardAnswer3.setOnClickListener(this::onAnswerClick);
        cardAnswer4.setOnClickListener(this::onAnswerClick);
    }


    private void showQuestion() {
        if (quizManager != null && quizManager.hasCurrentQuiz()) {
            Quiz quiz = quizManager.getCurrentQuiz();
            tvQuestion.setText(quiz.getQuestion());
            tvAnswer1.setText(quiz.getAnswer1());
            tvAnswer2.setText(quiz.getAnswer2());
            tvAnswer3.setText(quiz.getAnswer3());
            tvAnswer4.setText(quiz.getAnswer4());
            // Ghi nhận thời gian bắt đầu câu hỏi
            questionStartTime = System.currentTimeMillis();
            // Update progress
            updateProgress();
            // Reset answer state
            resetAnswerStates();
            // Start timer
            startTimer();
        }
    }
    
    private void updateProgress() {
        if (totalQuestions > 0) {
            int progress = (int) (((float) currentQuestionIndex / totalQuestions) * 100);
            progressBar.setProgress(progress);
            tvProgressText.setText("Câu " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        }
    }
    
    private void nextQuestion() {
        if (quizManager != null && quizManager.nextQuiz()) {
            currentQuestionIndex++;
            showQuestion();
        } else {
            // Quiz finished
            finishQuiz();
        }
    }
    
    private void finishQuiz() {
        // Sử dụng totalTimeSeconds đã cộng dồn
        Toast.makeText(this, "Grade: " + quizManager.getScore() + " - Thời gian: " + totalTimeSeconds + "s", Toast.LENGTH_SHORT).show();
        // Update leaderboard before navigating
        quizData.addOrUpdateLeaderboardEntry(
            quizSetName,
            currentUserName,
            quizManager.getScore(),
            quizManager.getScore10(),
            totalTimeSeconds,
            task -> {
                // Delay nhỏ để đảm bảo Firebase đã cập nhật xong
                new android.os.Handler().postDelayed(() -> {
                    Intent intent = new Intent(QuizActivity.this, LeaderboardActivity.class);
                    intent.putExtra("grade", quizManager.getScore());
                    intent.putExtra("grade10", quizManager.getScore10());
                    intent.putExtra("totalQuestions", totalQuestions);
                    intent.putExtra("quizSetName", quizSetName);
                    startActivity(intent);
                }, 500); // Delay 500ms
            }
        );
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        tvTimer.setText("30s");
        countDownTimer = new CountDownTimer(QUESTION_TIME_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(seconds + "s");
            }
            @Override
            public void onFinish() {
                tvTimer.setText("0s");
                if (!isAnswered) {
                    isAnswered = true;
                    // Tính thời gian trả lời câu hỏi này (hết giờ)
                    long answerTime = System.currentTimeMillis();
                    long questionTime = answerTime - questionStartTime;
                    totalTimeSeconds += (int) Math.ceil(questionTime / 1000.0);
                    Toast.makeText(QuizActivity.this, "Hết giờ!", Toast.LENGTH_SHORT).show();
                    showAnswerResult(-1); // -1 nghĩa là không chọn đáp án nào
                }
            }
        };
        countDownTimer.start();
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
        currentUserName = getIntent().getStringExtra("userName");
        if (currentUserName == null) {
            // Fallback to current user email if username not provided
            currentUserName = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        quizData = new QuizData();
        soundManager = new SoundManager(this);
        startTime = System.currentTimeMillis(); // Ghi nhận thời gian bắt đầu
        loadQuizData();
    }

    private void loadQuizData() {
        quizData.getQuizList(quizSetName,new QuizData.QuizDataCallback() {
            @Override
            public void onDataLoaded(List<Quiz> quizList) {
                quizManager = new QuizManager(quizList);
                totalQuestions = quizList.size();
                showQuestion();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(QuizActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onAnswerClick(View view) {
        if (isAnswered) return; // Prevent multiple clicks
        if (countDownTimer != null) countDownTimer.cancel();
        // Tính thời gian trả lời câu hỏi hiện tại
        long answerTime = System.currentTimeMillis();
        long questionTime = answerTime - questionStartTime;
        totalTimeSeconds += (int) Math.ceil(questionTime / 1000.0);
        int chosen = 0;
        if (view.getId() == R.id.cardAnswer1) chosen = 1;
        else if (view.getId() == R.id.cardAnswer2) chosen = 2;
        else if (view.getId() == R.id.cardAnswer3) chosen = 3;
        else if (view.getId() == R.id.cardAnswer4) chosen = 4;
        selectedAnswer = chosen;
        isAnswered = true;

        if (quizManager != null) {
            // Animate answer selection
            animateAnswerSelection(chosen);
            
            // Show result after animation
            final int chosenFinal = chosen;
            new Handler().postDelayed(() -> {
                showAnswerResult(chosenFinal);
            }, ANIMATION_DURATION);
        }
    }
    
    private void animateAnswerSelection(int selectedIdx) {
        CardView[] answerCards = {cardAnswer1, cardAnswer2, cardAnswer3, cardAnswer4};
        int selectedIndex = selectedIdx -1;
        for (int i = 0; i < answerCards.length; i++) {
            if (i == selectedIndex) {
                // Animate selected answer
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(answerCards[i], "scaleX", 1f, 1.05f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(answerCards[i], "scaleY", 1f, 1.05f);
                
                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.playTogether(scaleUpX, scaleUpY);
                scaleUp.setDuration(150);
                scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
                
                // Add selection background
                answerCards[i].setCardBackgroundColor(ContextCompat.getColor(this, R.color.purple_200));
                
                scaleUp.start();
            } else {
                // Fade out other answers
                answerCards[i].animate()
                        .alpha(0.5f)
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(150)
                        .start();
            }
        }
    }
    
    private void showAnswerResult(int chosenAnswer) {
        if (countDownTimer != null) countDownTimer.cancel();
        CardView[] answerCards = {cardAnswer1, cardAnswer2, cardAnswer3, cardAnswer4};
        Quiz currentQuiz = quizManager.getCurrentQuiz();
        int correctAnswer = currentQuiz.getCorrect();
        
        // Check answer and update score
        quizManager.checkAnswer(chosenAnswer);
        
        // Play sound based on answer result
        if (chosenAnswer == correctAnswer) {
            soundManager.playCorrectSound();
        } else {
            soundManager.playWrongSound();
        }
        
        // Update score display with animation
        animateScoreUpdate();
        
        for (int i = 0; i < answerCards.length; i++) {
            if (i == (correctAnswer - 1)) {
                answerCards[i].setCardBackgroundColor(ContextCompat.getColor(this, R.color.green_500));
                animateCorrectAnswer(answerCards[i]);
            } else if (i == (chosenAnswer - 1)) {
                answerCards[i].setCardBackgroundColor(ContextCompat.getColor(this, R.color.red_500));
                animateIncorrectAnswer(answerCards[i]);
            }
            
            // Restore alpha and scale
            answerCards[i].animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
        }
        
        // Show result message
        String message = chosenAnswer == correctAnswer ? 
            "🎉 Đúng! Chúc mừng bạn đã trả lời đúng" : 
            "❌ Sai! Đáp án đúng là: " + getAnswerText(correctAnswer);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        // Move to next question after delay
        new Handler().postDelayed(() -> {
            resetAnswerStates();
            nextQuestion();
        }, 2000);
    }
    
    private void animateCorrectAnswer(CardView cardView) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 1.1f, 1f);
        
        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulse, pulseY);
        pulseSet.setDuration(400);
        pulseSet.setInterpolator(new BounceInterpolator());
        pulseSet.start();
    }
    
    private void animateIncorrectAnswer(CardView cardView) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(cardView, "translationX", 
            0f, -20f, 20f, -15f, 15f, -10f, 10f, -5f, 5f, 0f);
        shake.setDuration(500);
        shake.start();
    }
    
    private void animateScoreUpdate() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvScore, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvScore, "scaleY", 1f, 1.3f, 1f);
        
        AnimatorSet scoreAnimation = new AnimatorSet();
        scoreAnimation.playTogether(scaleX, scaleY);
        scoreAnimation.setDuration(300);
        scoreAnimation.setInterpolator(new BounceInterpolator());
        scoreAnimation.start();
        
        // Update score text
        tvScore.setText("🏆 " + quizManager.getScore() + " điểm");
    }
    
    private void resetAnswerStates() {
        CardView[] answerCards = {cardAnswer1, cardAnswer2, cardAnswer3, cardAnswer4};
        for (CardView card : answerCards) {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
        isAnswered = false;
        selectedAnswer = -1;
    }
    
    private String getAnswerText(int answerIndex) {
        switch (answerIndex) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            default: return "";
        }
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (soundManager != null) {
            soundManager.release();
        }
        super.onDestroy();
    }
}



