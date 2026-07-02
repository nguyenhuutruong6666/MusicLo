package com.example.musicapp;

import android.content.Intent;
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

import com.example.musicapp.adapters.FavoriteMusicAdapter;
import com.example.musicapp.models.MusicModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * FavoriteMusicActivity - Màn hình danh sách nhạc yêu thích.
 * Lấy danh sách musicId từ favorites/{uid}, sau đó đọc thông tin từ songs.
 */
public class FavoriteMusicActivity extends AppCompatActivity {

    RecyclerView rvFavoriteList;
    ProgressBar progressBar;
    TextView tvEmpty;

    FavoriteMusicAdapter favoriteMusicAdapter;
    List<MusicModel> favoriteList;

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);

        rvFavoriteList = findViewById(R.id.rvFavoriteList);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        favoriteList = new ArrayList<>();

        favoriteMusicAdapter = new FavoriteMusicAdapter(this, favoriteList);
        rvFavoriteList.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteList.setAdapter(favoriteMusicAdapter);

        favoriteMusicAdapter.setOnItemClickListener(new FavoriteMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MusicModel music) {
                Intent intent = new Intent(FavoriteMusicActivity.this, MusicPlayerActivity.class);
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

        favoriteMusicAdapter.setOnRemoveFavoriteListener(new FavoriteMusicAdapter.OnRemoveFavoriteListener() {
            @Override
            public void onRemoveFavorite(MusicModel music) {
                removeFavorite(music);
            }
        });

        loadFavorites();
    }

    private void loadFavorites() {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        firebaseHelper.getUserFavoritesRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favoriteList.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    favoriteMusicAdapter.notifyDataSetChanged();
                    return;
                }

                final long totalCount = snapshot.getChildrenCount();
                final long[] loadedCount = {0};

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String musicId = ds.getKey();
                    if (musicId != null) {
                        firebaseHelper.getSongRef(musicId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot songSnapshot) {
                                loadedCount[0]++;
                                if (songSnapshot.exists()) {
                                    MusicModel music = songSnapshot.getValue(MusicModel.class);
                                    if (music != null) {
                                        favoriteList.add(music);
                                    }
                                }
                                if (loadedCount[0] >= totalCount) {
                                    progressBar.setVisibility(View.GONE);
                                    if (favoriteList.isEmpty()) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    } else {
                                        tvEmpty.setVisibility(View.GONE);
                                    }
                                    favoriteMusicAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                loadedCount[0]++;
                                if (loadedCount[0] >= totalCount) {
                                    progressBar.setVisibility(View.GONE);
                                    favoriteMusicAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FavoriteMusicActivity.this,
                        "Lỗi tải yêu thích: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavorite(MusicModel music) {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty()) return;

        firebaseHelper.getFavoriteRef(uid, music.getMusicId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FavoriteMusicActivity.this,
                                "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FavoriteMusicActivity.this,
                                "Lỗi bỏ yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
