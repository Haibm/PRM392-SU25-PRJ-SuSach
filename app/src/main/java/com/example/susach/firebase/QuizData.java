package com.example.susach.firebase;

import com.example.susach.models.Quiz;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizData {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface QuizDataCallback {
        void onDataLoaded(List<Quiz> quizList);
        void onError(Exception e);
    }

    public interface QuizSetCallback {
        void onDataLoaded(List<String> quizSetList);
        void onError(Exception e);
    }

    public void getQuizList(String quizSetName, QuizDataCallback callback) {
        db.collection("quizs")
            .document(quizSetName)
            .collection(quizSetName)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Quiz> quizList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String questionId = document.getId();
                        String question = document.getString("question");
                        String answer1 = document.getString("answer1");
                        String answer2 = document.getString("answer2");
                        String answer3 = document.getString("answer3");
                        String answer4 = document.getString("answer4");
                        int correct = document.getLong("correct").intValue();
                        Quiz quiz = new Quiz(questionId, question, answer1, answer2, answer3, answer4, correct);
                        quizList.add(quiz);
                    }
                    callback.onDataLoaded(quizList);
                } else {
                    callback.onError(task.getException());
                }
            });
    }

    public void getQuizSetList(QuizSetCallback callback) {
        db.collection("quizs")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> quizSetList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        quizSetList.add(document.getId());
                    }
                    callback.onDataLoaded(quizSetList);
                } else {
                    callback.onError(task.getException());
                }
            });
    }

    public void addQuizSet(String quizSetName, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        db.collection("quizs").document(quizSetName).set(new java.util.HashMap<>()).addOnCompleteListener(listener);
    }

    public void deleteQuizSet(String quizSetName, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        db.collection("quizs").document(quizSetName).delete().addOnCompleteListener(listener);
    }

    public void renameQuizSet(String oldName, String newName, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        db.collection("quizs").document(newName).set(new java.util.HashMap<>())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    db.collection("quizs").document(oldName).delete().addOnCompleteListener(listener);
                } else {
                    listener.onComplete(task);
                }
            });
    }

    public void addQuestion(String quizSetName, Quiz quiz, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        String questionId = db.collection("quizs").document(quizSetName)
            .collection(quizSetName).document().getId();
        db.collection("quizs").document(quizSetName)
            .collection(quizSetName).document(questionId)
            .set(quizToMap(quiz))
            .addOnCompleteListener(listener);
    }

    public void updateQuestion(String quizSetName, String questionId, Quiz quiz, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        db.collection("quizs").document(quizSetName)
            .collection(quizSetName).document(questionId)
            .set(quizToMap(quiz))
            .addOnCompleteListener(listener);
    }

    public void deleteQuestion(String quizSetName, String questionId, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        db.collection("quizs").document(quizSetName)
            .collection(quizSetName).document(questionId)
            .delete()
            .addOnCompleteListener(listener);
    }

    private java.util.Map<String, Object> quizToMap(Quiz quiz) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("question", quiz.getQuestion());
        map.put("answer1", quiz.getAnswer1());
        map.put("answer2", quiz.getAnswer2());
        map.put("answer3", quiz.getAnswer3());
        map.put("answer4", quiz.getAnswer4());
        map.put("correct", quiz.getCorrect());
        return map;
    }
} 