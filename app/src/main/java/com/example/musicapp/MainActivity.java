package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();

        if (currentUser == null) {
            // Chưa đăng nhập, chuyển sang LoginActivity
            goToLogin();
            return;
        }

        // Đã đăng nhập, đọc role từ Firebase
        String uid = currentUser.getUid();
        firebaseHelper.getUserRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                    if (role == null) role = "user";
                    if (fullName == null) fullName = "";
                    if (email == null) email = "";
                    if (avatarUrl == null) avatarUrl = "";

                    sessionManager.saveSession(uid, role, fullName, email, avatarUrl);

                    if ("admin".equals(role)) {
                        goToAdminDashboard();
                    } else {
                        goToMusicList();
                    }
                } else {
                    // Dữ liệu user không tồn tại trong DB, chuyển về login
                    firebaseHelper.signOut();
                    sessionManager.clearSession();
                    goToLogin();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Lỗi đọc DB, chuyển về login
                firebaseHelper.signOut();
                sessionManager.clearSession();
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToMusicList() {
        Intent intent = new Intent(MainActivity.this, MusicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdminDashboard() {
        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}