package com.example.susach.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.susach.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.susach.models.Account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccountInfoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgAvatar;
    private EditText etName;
    private EditText etEmail;
    private Spinner spinnerRole;
    private Button btnSave;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imgAvatar = findViewById(R.id.img_avatar_info);
        etName = findViewById(R.id.et_name_info);
        etEmail = findViewById(R.id.et_email_info);
        spinnerRole = findViewById(R.id.spinner_role_info);
        btnSave = findViewById(R.id.btn_save_info);
        btnLogout = findViewById(R.id.btn_logout_info);


        setupRoleSpinner();


        loadUserInfo();

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AccountInfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupRoleSpinner() {
        String[] roles = {"User", "Staff", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentEmail = currentUser.getEmail();
            etEmail.setText(currentEmail);

            db.collection("account").document(currentEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                DocumentSnapshot document = task.getResult();
                                // Use Account model
                                Account account = document.toObject(Account.class);
                                if (account != null) {
                                    etName.setText(account.getName());
                                    setRoleSpinnerSelection(account.getRole());
                                }
                            } else {
                                Toast.makeText(AccountInfoActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRoleSpinnerSelection(int role) {
        switch (role) {
            case 1:
                spinnerRole.setSelection(2);
                break;
            case 2:
                spinnerRole.setSelection(1);
                break;
            case 3:
            default:
                spinnerRole.setSelection(0);
                break;
        }
    }

    private int getSelectedRoleValue() {
        String selectedRole = spinnerRole.getSelectedItem().toString();
        switch (selectedRole) {
            case "Admin": return 1;
            case "Staff": return 2;
            case "User": 
            default: return 3;
        }
    }

    private void saveUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = etEmail.getText().toString().trim();
            String name = etName.getText().toString().trim();
            int role = getSelectedRoleValue();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use Account model
            Account account = new Account(email, null, name, role);
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", account.getEmail());
            userData.put("name", account.getName());
            userData.put("role", account.getRole());

            db.collection("account").document(currentEmail)
                    .update(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountInfoActivity.this, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show();
                                // Update current email if it changed
                                currentEmail = email;
                            } else {
                                Toast.makeText(AccountInfoActivity.this, "Lỗi khi lưu thay đổi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 