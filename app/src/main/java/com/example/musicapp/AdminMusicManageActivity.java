package com.example.musicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import com.example.musicapp.adapters.AdminMusicAdapter;
import com.example.musicapp.models.MusicModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminMusicManageActivity - Trang quản lý nhạc dành cho Admin.
 * Hiển thị danh sách nhạc, có nút thêm, sửa, xóa.
 */
public class AdminMusicManageActivity extends AppCompatActivity {

    RecyclerView rvMusicList;
    Button btnAddMusic;
    ProgressBar progressBar;
    TextView tvEmpty;

    AdminMusicAdapter adminMusicAdapter;
    List<MusicModel> musicList;

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_music_manage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();

        rvMusicList = findViewById(R.id.rvMusicList);
        btnAddMusic = findViewById(R.id.btnAddMusic);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        musicList = new ArrayList<>();

        adminMusicAdapter = new AdminMusicAdapter(this, musicList);
        rvMusicList.setLayoutManager(new LinearLayoutManager(this));
        rvMusicList.setAdapter(adminMusicAdapter);

        adminMusicAdapter.setOnEditClickListener(new AdminMusicAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(MusicModel music) {
                Intent intent = new Intent(AdminMusicManageActivity.this, AddEditMusicActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("musicId", music.getMusicId());
                intent.putExtra("title", music.getTitle());
                intent.putExtra("artist", music.getArtist());
                intent.putExtra("category", music.getCategory());
                intent.putExtra("description", music.getDescription());
                intent.putExtra("imageUrl", music.getImageUrl());
                intent.putExtra("mp3Url", music.getMp3Url());
                startActivity(intent);
            }
        });

        adminMusicAdapter.setOnDeleteClickListener(new AdminMusicAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(MusicModel music) {
                showDeleteConfirmDialog(music);
            }
        });

        btnAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMusicManageActivity.this, AddEditMusicActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });

        loadMusicList();
    }

    private void loadMusicList() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        firebaseHelper.getSongsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                musicList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MusicModel music = ds.getValue(MusicModel.class);
                    if (music != null) {
                        musicList.add(music);
                    }
                }
                progressBar.setVisibility(View.GONE);
                if (musicList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                adminMusicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminMusicManageActivity.this,
                        "Lỗi tải danh sách nhạc: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmDialog(MusicModel music) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMusicManageActivity.this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có muốn xóa bài hát \"" + music.getTitle() + "\" không?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMusic(music);
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

    private void deleteMusic(MusicModel music) {
        firebaseHelper.getSongRef(music.getMusicId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMusicManageActivity.this,
                                "Đã xóa bài hát \"" + music.getTitle() + "\"",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminMusicManageActivity.this,
                                "Lỗi xóa bài hát", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Danh sách sẽ tự cập nhật do dùng addValueEventListener
    }
}
