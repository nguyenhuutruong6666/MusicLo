package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;

/**
 * AdminDashboardActivity - Trang chính của Admin.
 * Cung cấp các nút điều hướng đến quản lý nhạc, quản lý user và đăng xuất.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    Button btnManageMusic;
    Button btnManageUsers;
    Button btnLogout;

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);

        btnManageMusic = findViewById(R.id.btnManageMusic);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnLogout = findViewById(R.id.btnLogout);

        btnManageMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminMusicManageActivity.class);
                startActivity(intent);
            }
        });

        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminUserManageActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    private void handleLogout() {
        firebaseHelper.signOut();
        sessionManager.clearSession();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
