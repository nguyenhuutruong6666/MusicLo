package com.example.musicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.adapters.UserAdapter;
import com.example.musicapp.models.UserModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminUserManageActivity - Trang quản lý người dùng dành cho Admin.
 *
 * LƯU Ý QUAN TRỌNG:
 * Chức năng xóa ở đây chỉ xóa dữ liệu user khỏi Realtime Database.
 * Để xóa tài khoản Firebase Authentication, bắt buộc phải dùng
 * Firebase Admin SDK (backend) hoặc Cloud Functions.
 * Không thể xóa tài khoản Auth của user khác từ phía client.
 */
public class AdminUserManageActivity extends AppCompatActivity {

    RecyclerView rvUserList;
    ProgressBar progressBar;
    TextView tvEmpty;

    UserAdapter userAdapter;
    List<UserModel> userList;

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_manage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();

        rvUserList = findViewById(R.id.rvUserList);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        userList = new ArrayList<>();

        userAdapter = new UserAdapter(this, userList);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setAdapter(userAdapter);

        userAdapter.setOnRoleChangeListener(new UserAdapter.OnRoleChangeListener() {
            @Override
            public void onRoleChange(UserModel user, String newRole) {
                showRoleChangeConfirmDialog(user, newRole);
            }
        });

        userAdapter.setOnDeleteUserListener(new UserAdapter.OnDeleteUserListener() {
            @Override
            public void onDeleteUser(UserModel user) {
                showDeleteUserConfirmDialog(user);
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        firebaseHelper.getUsersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserModel user = ds.getValue(UserModel.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                progressBar.setVisibility(View.GONE);
                if (userList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminUserManageActivity.this,
                        "Lỗi tải danh sách user: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRoleChangeConfirmDialog(UserModel user, String newRole) {
        String newRoleText = "admin".equals(newRole) ? "Admin" : "User";

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminUserManageActivity.this);
        builder.setTitle("Đổi quyền");
        builder.setMessage("Bạn có muốn đổi quyền của \"" + user.getFullName() + "\" thành " + newRoleText + " không?");

        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeUserRole(user, newRole);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void changeUserRole(UserModel user, String newRole) {
        firebaseHelper.getUserRef(user.getUid()).child("role").setValue(newRole)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminUserManageActivity.this,
                                "Đã đổi quyền của \"" + user.getFullName() + "\" thành " + newRole,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminUserManageActivity.this,
                                "Lỗi đổi quyền", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteUserConfirmDialog(UserModel user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminUserManageActivity.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có muốn xóa user \"" + user.getFullName() + "\" không?\n\n"
                + "⚠️ Lưu ý: Chỉ xóa dữ liệu khỏi Database. "
                + "Để xóa tài khoản Authentication, cần dùng Firebase Admin SDK (backend).");

        builder.setPositiveButton("Xóa Database", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserFromDatabase(user);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    private void deleteUserFromDatabase(UserModel user) {
        firebaseHelper.getUserRef(user.getUid()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminUserManageActivity.this,
                                "Đã xóa dữ liệu của \"" + user.getFullName() + "\" khỏi Database",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminUserManageActivity.this,
                                "Lỗi xóa user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
