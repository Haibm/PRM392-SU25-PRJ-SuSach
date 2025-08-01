package com.example.susach.firebase;

import com.example.susach.models.Quiz;
import com.example.susach.models.Leaderboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public interface LeaderboardDataCallback {
        void onDataLoaded(List<Leaderboard> leaderboardList);
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

    public void getLeaderboardList(String quizSetName, LeaderboardDataCallback callback) {
        db.collection("leaderboard")
            .document(quizSetName)
            .collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Leaderboard> leaderboardList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            String name = document.getString("name");
                            int grade = document.getLong("grade").intValue();
                            float grade10 = document.getDouble("grade10").floatValue();
                            int totalTime = document.getLong("totalTime") != null ? 
                                document.getLong("totalTime").intValue() : 0;
                            
                            // Debug log để kiểm tra dữ liệu đọc từ Firebase
                            android.util.Log.d("QuizData", "Reading from Firebase: " +
                                "Name=" + name +
                                ", Grade=" + grade +
                                ", Grade10=" + grade10 +
                                ", TotalTime=" + totalTime + "s");
                            
                            if (name != null) {
                                leaderboardList.add(new Leaderboard(name, grade, grade10, totalTime));
                            }
                        } catch (Exception e) {
                            // skip malformed document
                            android.util.Log.e("QuizData", "Error reading document: " + e.getMessage());
                        }
                    }
                    // Sắp xếp theo điểm cao nhất, nếu bằng nhau thì theo thời gian ngắn nhất
                    leaderboardList.sort((o1, o2) -> {
                        if (o2.getGrade10() != o1.getGrade10()) {
                            return Float.compare(o2.getGrade10(), o1.getGrade10());
                        } else {
                            return Integer.compare(o1.getTotalTime(), o2.getTotalTime());
                        }
                    });
                    
                    // Chỉ lấy Top 15 người làm đúng và nhanh nhất
                    List<Leaderboard> top15List = new ArrayList<>();
                    for (int i = 0; i < Math.min(15, leaderboardList.size()); i++) {
                        top15List.add(leaderboardList.get(i));
                    }
                    
                    callback.onDataLoaded(top15List);
                } else {
                    callback.onError(task.getException());
                }
            });
    }

    /**
     * Add or update a user's leaderboard entry. If the user already exists and the new score is better, update it.
     * Otherwise, add a new entry.
     */
    public void addOrUpdateLeaderboardEntry(String quizSetName, String userName, int grade, float grade10, int totalTime, OnCompleteListener<Void> listener) {
        db.collection("leaderboard")
                .document(quizSetName)
                .set(new HashMap<>())  // Tạo document cha rõ ràng
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("leaderboard")
                                .document(quizSetName)
                                .collection("users")
                                .document(userName)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    boolean shouldUpdate = true;
                                    if (documentSnapshot.exists()) {
                                        Integer oldGrade = documentSnapshot.getLong("grade") != null ? documentSnapshot.getLong("grade").intValue() : null;
                                        Float oldGrade10 = documentSnapshot.getDouble("grade10") != null ? documentSnapshot.getDouble("grade10").floatValue() : null;
                                        Integer oldTotalTime = documentSnapshot.getLong("totalTime") != null ? documentSnapshot.getLong("totalTime").intValue() : null;
                                        
                                        if (oldGrade10 != null) {
                                            if (grade10 > oldGrade10) {
                                                // Điểm mới cao hơn
                                                shouldUpdate = true;
                                            } else if (grade10 == oldGrade10) {
                                                // Điểm bằng nhau, so sánh thời gian
                                                if (oldTotalTime != null && totalTime < oldTotalTime) {
                                                    // Thời gian mới ngắn hơn
                                                    shouldUpdate = true;
                                                } else {
                                                    shouldUpdate = false;
                                                }
                                            } else {
                                                shouldUpdate = false;
                                            }
                                        }
                                    }
                                    if (shouldUpdate) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("name", userName);
                                        data.put("grade", grade);
                                        data.put("grade10", (double) grade10);
                                        data.put("totalTime", totalTime);
                                        
                                        // Debug log để kiểm tra dữ liệu lưu
                                        android.util.Log.d("QuizData", "Saving to Firebase: " +
                                            "Name=" + userName +
                                            ", Grade=" + grade +
                                            ", Grade10=" + grade10 +
                                            ", TotalTime=" + totalTime + "s");
                                        
                                        db.collection("leaderboard")
                                                .document(quizSetName)
                                                .collection("users")
                                                .document(userName)
                                                .set(data)
                                                .addOnCompleteListener(listener);
                                    } else {
                                        if (listener != null) listener.onComplete(null);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) listener.onComplete(null);
                                });
                    } else {
                        if (listener != null) listener.onComplete(task);
                    }
                });
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