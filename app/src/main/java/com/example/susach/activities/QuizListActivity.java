package com.example.susach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.susach.R;
import com.example.susach.adapters.QuestionAdapter;
import com.example.susach.firebase.QuizData;
import com.example.susach.models.Quiz;
import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity implements EditQuestionDialogFragment.EditQuestionListener {
    private RecyclerView rcvQuestion;
    private View btnAddQuestion;
    private QuestionAdapter adapter;
    private List<Quiz> questionList = new ArrayList<>();
    private QuizData quizData;
    private String quizSetName;
    private Quiz editingQuiz = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        bindingView();
        bindingAction();
        loadQuestionList();
    }

    private void bindingView() {
        rcvQuestion = findViewById(R.id.rcvQuestion);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        quizData = new QuizData();
        adapter = new QuestionAdapter(questionList, new QuestionAdapter.OnQuestionClickListener() {
            @Override
            public void onQuestionClick(Quiz quiz) {
                onQuestionClickHandler(quiz);
            }
            @Override
            public void onDeleteQuestion(Quiz quiz, int position) {
                quizData.deleteQuestion(quizSetName, quiz.getQuestionId(), task -> {
                    if (task.isSuccessful()) {
                        questionList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(QuizListActivity.this, "Đã xóa câu hỏi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuizListActivity.this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        rcvQuestion.setLayoutManager(new LinearLayoutManager(this));
        rcvQuestion.setAdapter(adapter);
        Intent intent = getIntent();
        quizSetName = intent.getStringExtra("quizSetName");
    }

    private void bindingAction() {
        btnAddQuestion.setOnClickListener(this::onBtnAddQuestionClick);
    }

    private void onBtnAddQuestionClick(View v) {
        editingQuiz = null;
        EditQuestionDialogFragment dialog = EditQuestionDialogFragment.newInstance(null);
        dialog.show(getSupportFragmentManager(), "add_question");
    }

    private void onQuestionClickHandler(Quiz quiz) {
        editingQuiz = quiz;
        EditQuestionDialogFragment dialog = EditQuestionDialogFragment.newInstance(quiz);
        dialog.show(getSupportFragmentManager(), "edit_question");
    }

    @Override
    public void onSaveQuestion(Quiz quiz, boolean isEdit) {
        if (isEdit && editingQuiz != null) {
            quizData.updateQuestion(quizSetName, editingQuiz.getQuestionId(), quiz, task -> {
                if (task.isSuccessful()) {
                    loadQuestionList();
                    Toast.makeText(this, "Đã cập nhật câu hỏi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            quizData.addQuestion(quizSetName, quiz, task -> {
                if (task.isSuccessful()) {
                    loadQuestionList();
                    Toast.makeText(this, "Đã thêm câu hỏi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadQuestionList() {
        quizData.getQuizList(quizSetName, new QuizData.QuizDataCallback() {
            @Override
            public void onDataLoaded(List<Quiz> quizList) {
                questionList.clear();
                questionList.addAll(quizList);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(QuizListActivity.this, "Lỗi tải danh sách câu hỏi", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 