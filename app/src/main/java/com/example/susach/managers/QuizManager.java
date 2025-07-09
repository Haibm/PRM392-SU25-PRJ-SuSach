package com.example.susach.managers;

import com.example.susach.models.Quiz;

import java.util.List;

public class QuizManager {
    private List<Quiz> quizList;
    private int currentIndex = 0;
    private int score = 0;

    public QuizManager(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    public Quiz getCurrentQuiz() {
        if (quizList != null && currentIndex < quizList.size()) {
            return quizList.get(currentIndex);
        }
        return null;
    }

    public boolean hasCurrentQuiz() {
        return quizList != null && currentIndex < quizList.size();
    }

    public void checkAnswer(int answerIndex) {
        Quiz quiz = getCurrentQuiz();
        if (quiz != null && answerIndex == quiz.getCorrect()) {
            score++;
        }
    }

    public boolean nextQuiz() {
        if (quizList != null && currentIndex < quizList.size() - 1) {
            currentIndex++;
            return true;
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuiz() {
        return quizList != null ? quizList.size() : 0;
    }
} 