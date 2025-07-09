package com.example.susach.managers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AuthManager {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final Activity activity;

    public interface AuthCallback {
        void onSuccess(int role);
        void onFailure(String message);
    }

    public AuthManager(Activity activity) {
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public void login(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    db.collection("account").document(email)
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful() && task2.getResult().exists()) {
                                DocumentSnapshot doc = task2.getResult();
                                Long roleLong = doc.getLong("role");
                                int role = (roleLong != null) ? roleLong.intValue() : 3;
                                callback.onSuccess(role);
                            } else {
                                callback.onFailure("Không tìm thấy thông tin tài khoản!");
                            }
                        });
                } else {
                    callback.onFailure("Đăng nhập thất bại: " + task.getException().getMessage());
                }
            });
    }

    public void signup(String email, String name, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    // Tạo user object
                    java.util.Map<String, Object> user = new java.util.HashMap<>();
                    user.put("email", email);
                    user.put("name", name);
                    user.put("password", password);
                    user.put("role", 3); // Mặc định user thường
                    db.collection("account").document(email)
                        .set(user)
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                callback.onSuccess(3);
                            } else {
                                callback.onFailure("Lỗi lưu thông tin tài khoản: " + task2.getException().getMessage());
                            }
                        });
                } else {
                    callback.onFailure("Đăng ký thất bại: " + task.getException().getMessage());
                }
            });
    }

    public static void redirectBasedOnRole(Activity activity, int role) {
        Intent intent;
        switch (role) {
            case 1:
                intent = new Intent(activity, com.example.susach.activities.AdminActivity.class);
                break;
            case 2:
                intent = new Intent(activity, com.example.susach.activities.StaffActivity.class);
                break;
            case 3:
            default:
                intent = new Intent(activity, com.example.susach.activities.UserHomeActivity.class);
                break;
        }
        activity.startActivity(intent);
        activity.finish();
    }
} 