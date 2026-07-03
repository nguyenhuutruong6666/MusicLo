package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.models.UserModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.google.firebase.auth.AuthResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RegisterActivity - Màn hình đăng ký tài khoản.
 * Tạo tài khoản Firebase Auth và lưu thông tin vào Realtime Database.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtConfirmPassword;
    Button btnRegister;
    TextView tvGoToLogin;
    ProgressBar progressBar;

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        firebaseHelper.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        AuthResult result = task.getResult();
                        if (result != null && result.getUser() != null) {
                            String uid = result.getUser().getUid();
                            saveUserToDatabase(uid, fullName, email);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Exception exception = task.getException();
                        String errorMsg = "Đăng ký thất bại";

                        if (exception instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            errorMsg = "Email này đã được sử dụng. Vui lòng chọn email khác.";
                        } else if (exception != null) {
                            String msg = exception.getMessage();
                            if (msg != null) {
                                if (msg.contains("badly formatted")) {
                                    errorMsg = "Định dạng email không hợp lệ.";
                                } else if (msg.contains("network error")) {
                                    errorMsg = "Lỗi mạng. Vui lòng kiểm tra kết nối internet.";
                                } else {
                                    errorMsg = "Lỗi đăng ký: " + msg;
                                }
                            }
                        }
                        
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String uid, String fullName, String email) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        UserModel userModel = new UserModel(uid, fullName, email, "user", "", createdAt);

        firebaseHelper.getUserRef(uid).setValue(userModel)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thành công! Vui lòng đăng nhập.",
                                Toast.LENGTH_SHORT).show();
                        // Đăng xuất để user phải đăng nhập lại (optional)
                        firebaseHelper.signOut();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Lỗi lưu dữ liệu";
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
