package com.example.musicapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musicapp.models.FavoriteModel;
import com.example.musicapp.utils.FirebaseHelper;
import com.example.musicapp.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MusicPlayerActivity extends AppCompatActivity {

    ImageView ivCover;
    TextView tvTitle;
    TextView tvArtist;
    TextView tvCategory;
    TextView tvDescription;
    ImageButton btnPlayPause;
    ImageButton btnStop;
    ImageButton btnFavorite;
    ImageButton btnBack;
    SeekBar seekBar;
    TextView tvCurrentTime;
    TextView tvTotalTime;
    ProgressBar progressBar;

    MediaPlayer mediaPlayer;
    Handler seekBarHandler;
    Runnable seekBarRunnable;

    boolean isPlaying = false;
    boolean isFavorite = false;
    boolean isPrepared = false;

    String musicId;
    String title;
    String artist;
    String category;
    String description;
    String imageUrl;
    String mp3Url;

    FirebaseHelper firebaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        sessionManager = new SessionManager(this);
        seekBarHandler = new Handler(Looper.getMainLooper());

        ivCover = findViewById(R.id.ivCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBack = findViewById(R.id.btnBack);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        progressBar = findViewById(R.id.progressBar);

        // Nhận dữ liệu từ Intent
        Bundle data = getIntent().getExtras();
        if (data != null) {
            musicId = data.getString("musicId", "");
            title = data.getString("title", "");
            artist = data.getString("artist", "");
            category = data.getString("category", "");
            description = data.getString("description", "");
            imageUrl = data.getString("imageUrl", "");
            mp3Url = data.getString("mp3Url", "");
        }

        displayMusicInfo();
        checkFavoriteStatus();
        setupMediaPlayer();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayPause();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStop();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && isPrepared) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void displayMusicInfo() {
        tvTitle.setText(title != null ? title : "");
        tvArtist.setText(artist != null ? artist : "");
        tvCategory.setText(category != null ? category : "");
        tvDescription.setText(description != null ? description : "");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(ivCover);
        } else {
            ivCover.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void setupMediaPlayer() {
        if (mp3Url == null || mp3Url.isEmpty()) {
            Toast.makeText(this, "Không có file nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPlayPause.setEnabled(false);

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mp3Url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                btnPlayPause.setEnabled(true);
                isPrepared = true;

                int duration = mediaPlayer.getDuration();
                seekBar.setMax(duration);
                tvTotalTime.setText(formatTime(duration));
                tvCurrentTime.setText("0:00");

                // Tự động phát khi sẵn sàng
                mediaPlayer.start();
                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                startSeekBarUpdate();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                isPrepared = false;
                btnPlayPause.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                tvCurrentTime.setText("0:00");
                stopSeekBarUpdate();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                progressBar.setVisibility(View.GONE);
                btnPlayPause.setEnabled(true);
                Toast.makeText(MusicPlayerActivity.this,
                        "Lỗi phát nhạc: " + what, Toast.LENGTH_SHORT).show();
                return true;
            });

        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            btnPlayPause.setEnabled(true);
            Toast.makeText(this, "Lỗi tải nhạc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePlayPause() {
        if (mediaPlayer == null) {
            setupMediaPlayer();
            return;
        }

        if (!isPrepared) {
            Toast.makeText(this, "Đang tải nhạc, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            stopSeekBarUpdate();
        } else {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            startSeekBarUpdate();
        }
    }

    private void handleStop() {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.stop();
            isPlaying = false;
            isPrepared = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            seekBar.setProgress(0);
            tvCurrentTime.setText("0:00");
            stopSeekBarUpdate();

            // Chuẩn bị lại để có thể phát tiếp
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mp3Url);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    isPrepared = true;
                    int duration = mediaPlayer.getDuration();
                    seekBar.setMax(duration);
                    tvTotalTime.setText(formatTime(duration));
                });
            } catch (IOException e) {
                Toast.makeText(this, "Lỗi reset nhạc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkFavoriteStatus() {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty() || musicId == null || musicId.isEmpty()) return;

        firebaseHelper.getFavoriteRef(uid, musicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                updateFavoriteButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void toggleFavorite() {
        String uid = sessionManager.getUid();
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            firebaseHelper.getFavoriteRef(uid, musicId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isFavorite = false;
                            updateFavoriteButton();
                            Toast.makeText(MusicPlayerActivity.this,
                                    "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MusicPlayerActivity.this,
                                    "Lỗi bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String addedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            FavoriteModel favoriteModel = new FavoriteModel(musicId, addedAt);
            firebaseHelper.getFavoriteRef(uid, musicId).setValue(favoriteModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isFavorite = true;
                            updateFavoriteButton();
                            Toast.makeText(MusicPlayerActivity.this,
                                    "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MusicPlayerActivity.this,
                                    "Lỗi thêm yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateFavoriteButton() {
        btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );
    }

    private void startSeekBarUpdate() {
        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying && isPrepared) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    seekBarHandler.postDelayed(this, 500);
                }
            }
        };
        seekBarHandler.postDelayed(seekBarRunnable, 500);
    }

    private void stopSeekBarUpdate() {
        if (seekBarRunnable != null) {
            seekBarHandler.removeCallbacks(seekBarRunnable);
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSeekBarUpdate();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            stopSeekBarUpdate();
        }
    }
}
