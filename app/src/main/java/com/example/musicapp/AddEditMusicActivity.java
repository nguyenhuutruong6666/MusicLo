package com.example.musicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.bumptech.glide.Glide;
import com.example.musicapp.models.MusicModel;
import com.example.musicapp.utils.CloudinaryUploader;
import com.example.musicapp.utils.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEditMusicActivity extends AppCompatActivity {

    ImageView ivCoverPreview;
    ImageButton btnBack;
    Button btnSelectImage;
    Button btnSelectMp3;
    TextView tvMp3FileName;
    EditText edtTitle;
    EditText edtArtist;
    EditText edtCategory;
    EditText edtDescription;
    Button btnSave;
    ProgressBar progressBar;
    TextView tvProgressStatus;

    Uri selectedImageUri = null;
    Uri selectedMp3Uri = null;

    String mode = "add"; // "add" hoặc "edit"
    String musicId = "";
    String existingImageUrl = "";
    String existingMp3Url = "";

    FirebaseHelper firebaseHelper;
    CloudinaryUploader cloudinaryUploader;

    // Launcher chọn ảnh
    ActivityResultLauncher<String> imagePickerLauncher;
    // Launcher chọn mp3
    ActivityResultLauncher<String> mp3PickerLauncher;
    // Launcher xin quyền
    ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = FirebaseHelper.getInstance();
        cloudinaryUploader = new CloudinaryUploader();

        ivCoverPreview = findViewById(R.id.ivCoverPreview);
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectMp3 = findViewById(R.id.btnSelectMp3);
        tvMp3FileName = findViewById(R.id.tvMp3FileName);
        edtTitle = findViewById(R.id.edtTitle);
        edtArtist = findViewById(R.id.edtArtist);
        edtCategory = findViewById(R.id.edtCategory);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
        tvProgressStatus = findViewById(R.id.tvProgressStatus);

        // Đăng ký launcher chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            selectedImageUri = uri;
                            Glide.with(AddEditMusicActivity.this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(ivCoverPreview);
                        }
                    }
                }
        );

        // Đăng ký launcher chọn mp3
        mp3PickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            selectedMp3Uri = uri;
                            tvMp3FileName.setText("Đã chọn file mp3");
                            tvMp3FileName.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // Đăng ký launcher xin quyền
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean imageGranted = result.getOrDefault(
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                    ? Manifest.permission.READ_MEDIA_IMAGES
                                    : Manifest.permission.READ_EXTERNAL_STORAGE,
                            false
                    );
                    if (imageGranted != null && imageGranted) {
                        Toast.makeText(this, "Quyền đã được cấp", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Cần cấp quyền để chọn file", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Lấy dữ liệu từ Intent (chế độ edit)
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mode = data.getString("mode", "add");
            musicId = data.getString("musicId", "");
            existingImageUrl = data.getString("imageUrl", "");
            existingMp3Url = data.getString("mp3Url", "");

            if ("edit".equals(mode)) {
                edtTitle.setText(data.getString("title", ""));
                edtArtist.setText(data.getString("artist", ""));
                edtCategory.setText(data.getString("category", ""));
                edtDescription.setText(data.getString("description", ""));
                btnSave.setText("Cập nhật");

                if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(existingImageUrl)
                            .centerCrop()
                            .into(ivCoverPreview);
                }
                if (existingMp3Url != null && !existingMp3Url.isEmpty()) {
                    tvMp3FileName.setText("Đã có file mp3 (chọn lại để thay đổi)");
                    tvMp3FileName.setVisibility(View.VISIBLE);
                }
            }
        }

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSelectMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickMp3();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveConfirmDialog();
            }
        });
    }

    private void showSaveConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận lưu")
                .setMessage("Bạn có chắc chắn muốn lưu thông tin bài hát này không?")
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleSave();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*");
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    private void checkPermissionAndPickMp3() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                mp3PickerLauncher.launch("audio/*");
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_AUDIO});
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                mp3PickerLauncher.launch("audio/*");
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        }
    }

    private void handleSave() {
        String title = edtTitle.getText().toString().trim();
        String artist = edtArtist.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        if (artist.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên ca sĩ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu thêm mới, bắt buộc phải chọn ảnh và mp3
        if ("add".equals(mode)) {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh bìa bài hát", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMp3Uri == null) {
                Toast.makeText(this, "Vui lòng chọn file mp3", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        tvProgressStatus.setVisibility(View.VISIBLE);

        // Xác định có cần upload ảnh mới không
        boolean needUploadImage = selectedImageUri != null;
        boolean needUploadMp3 = selectedMp3Uri != null;

        if (needUploadImage) {
            tvProgressStatus.setText("Đang upload ảnh bìa...");
            cloudinaryUploader.uploadImage(this, selectedImageUri, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    if (needUploadMp3) {
                        tvProgressStatus.setText("Đang upload file mp3...");
                        cloudinaryUploader.uploadMp3(AddEditMusicActivity.this, selectedMp3Uri, new CloudinaryUploader.UploadCallback() {
                            @Override
                            public void onSuccess(String mp3Url) {
                                saveMusicToFirebase(title, artist, category, description, imageUrl, mp3Url);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                progressBar.setVisibility(View.GONE);
                                btnSave.setEnabled(true);
                                tvProgressStatus.setVisibility(View.GONE);
                                Toast.makeText(AddEditMusicActivity.this,
                                        "Lỗi upload mp3: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // Không upload mp3 mới, dùng url cũ
                        saveMusicToFirebase(title, artist, category, description, imageUrl, existingMp3Url);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    tvProgressStatus.setVisibility(View.GONE);
                    Toast.makeText(AddEditMusicActivity.this,
                            "Lỗi upload ảnh: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else if (needUploadMp3) {
            tvProgressStatus.setText("Đang upload file mp3...");
            cloudinaryUploader.uploadMp3(this, selectedMp3Uri, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String mp3Url) {
                    saveMusicToFirebase(title, artist, category, description, existingImageUrl, mp3Url);
                }

                @Override
                public void onError(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    tvProgressStatus.setVisibility(View.GONE);
                    Toast.makeText(AddEditMusicActivity.this,
                            "Lỗi upload mp3: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Không upload gì cả, chỉ cập nhật text
            saveMusicToFirebase(title, artist, category, description, existingImageUrl, existingMp3Url);
        }
    }

    private void saveMusicToFirebase(String title, String artist, String category,
                                      String description, String imageUrl, String mp3Url) {
        tvProgressStatus.setText("Đang lưu vào Firebase...");

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if ("add".equals(mode)) {
            // Tạo ID mới
            DatabaseReference newRef = firebaseHelper.getSongsRef().push();
            String newMusicId = newRef.getKey();

            MusicModel musicModel = new MusicModel(
                    newMusicId, title, artist, category, description, imageUrl, mp3Url, now, now
            );

            newRef.setValue(musicModel)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        tvProgressStatus.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(AddEditMusicActivity.this,
                                    "Thêm bài hát thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(AddEditMusicActivity.this,
                                    "Lỗi lưu dữ liệu: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // Cập nhật bài hát đã tồn tại
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title);
            updates.put("artist", artist);
            updates.put("category", category);
            updates.put("description", description);
            updates.put("imageUrl", imageUrl);
            updates.put("mp3Url", mp3Url);
            updates.put("updatedAt", now);

            firebaseHelper.getSongRef(musicId).updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        tvProgressStatus.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(AddEditMusicActivity.this,
                                    "Cập nhật bài hát thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(AddEditMusicActivity.this,
                                    "Lỗi cập nhật: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
