package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;

/**
 * ProfileActivity - Màn hình hồ sơ người dùng.
 * Hiển thị thông tin cá nhân và nút đăng xuất.
 */
public class ProfileActivity extends AppCompatActivity {

    ImageView ivAvatar;
    TextView tvFullName;
    TextView tvEmail;
    TextView tvRole;
    Button btnLogout;

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserInfo();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    private void loadUserInfo() {
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();
        String avatarUrl = sessionManager.getAvatarUrl();

        tvFullName.setText(fullName != null && !fullName.isEmpty() ? fullName : "Chưa cập nhật");
        tvEmail.setText(email != null && !email.isEmpty() ? email : "Chưa cập nhật");
        tvRole.setText("Quyền: " + ("admin".equals(role) ? "Quản trị viên" : "Người dùng"));

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    private void handleLogout() {
        firebaseHelper.signOut();
        sessionManager.clearSession();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
