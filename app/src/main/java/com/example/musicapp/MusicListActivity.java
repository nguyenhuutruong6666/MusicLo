package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.example.musicapp.adapters.MusicAdapter;
import com.example.musicapp.models.FavoriteModel;
import com.example.musicapp.models.MusicModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MusicListActivity - Màn hình danh sách nhạc.
 * Hiển thị toàn bộ danh sách nhạc từ Firebase, có tìm kiếm và nút yêu thích.
 */
public class MusicListActivity extends AppCompatActivity {

    RecyclerView rvMusicList;
    EditText edtSearch;
    ProgressBar progressBar;
    TextView tvEmpty;
    ImageButton btnFavorite;
    ImageButton btnProfile;

    MusicAdapter musicAdapter;
    List<MusicModel> musicList;
    List<String> favoriteIds;

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);

        rvMusicList = findViewById(R.id.rvMusicList);
        edtSearch = findViewById(R.id.edtSearch);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnProfile = findViewById(R.id.btnProfile);

        musicList = new ArrayList<>();
        favoriteIds = new ArrayList<>();

        musicAdapter = new MusicAdapter(this, musicList);
        rvMusicList.setLayoutManager(new LinearLayoutManager(this));
        rvMusicList.setAdapter(musicAdapter);

        musicAdapter.setOnMusicClickListener(new MusicAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(MusicModel music) {
                Intent intent = new Intent(MusicListActivity.this, MusicPlayerActivity.class);
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

        musicAdapter.setOnFavoriteClickListener(new MusicAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(MusicModel music, boolean isFavorite) {
                toggleFavorite(music, isFavorite);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                musicAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicListActivity.this, FavoriteMusicActivity.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicListActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        loadFavorites();
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

                musicAdapter.setFullList(musicList);
                musicAdapter.setFavoriteIds(favoriteIds);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MusicListActivity.this,
                        "Lỗi tải danh sách nhạc: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavorites() {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty()) return;

        firebaseHelper.getUserFavoritesRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favoriteIds.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    favoriteIds.add(ds.getKey());
                }
                musicAdapter.setFavoriteIds(favoriteIds);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MusicListActivity.this,
                        "Lỗi tải yêu thích: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite(MusicModel music, boolean isFavorite) {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            // Bỏ yêu thích
            firebaseHelper.getFavoriteRef(uid, music.getMusicId()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MusicListActivity.this,
                                    "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MusicListActivity.this,
                                    "Lỗi bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Thêm yêu thích
            String addedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            FavoriteModel favoriteModel = new FavoriteModel(music.getMusicId(), addedAt);
            firebaseHelper.getFavoriteRef(uid, music.getMusicId()).setValue(favoriteModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MusicListActivity.this,
                                    "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MusicListActivity.this,
                                    "Lỗi thêm yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
