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
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import com.bumptech.glide.Glide;
import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    BottomNavigationView bottomNav;

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
        bottomNav = findViewById(R.id.bottomNav);

        loadUserInfo();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmDialog();
            }
        });

        // Setup Bottom Navigation
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, MusicListActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(ProfileActivity.this, FavoriteMusicActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
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
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleLogout();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
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
