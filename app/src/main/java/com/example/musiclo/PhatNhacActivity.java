package com.example.musiclo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.MusicPlayerManager;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhatNhacActivity extends AppCompatActivity {

    ImageView ivHinhAnhLinhVat;
    TextView tvTenBaiHat, tvCaSi, tvTheLoai, tvMoTa, tvThoiGianHienTai, tvTongThoiGian;
    ImageButton btnPhat, btnDung, btnYeuThich, btnQuayLai, btnBaiTruoc, btnBaiTiep;
    SeekBar seekBar;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    boolean dangPhat = false;
    boolean daYeuThich = false;

    // Danh sách phát
    List<BaiHat> danhSachBaiHat;
    int viTriHienTai = -1;

    // Dữ liệu bài hát hiện tại
    BaiHat baiHatHienTai = null;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phat_nhac);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        ivHinhAnhLinhVat = findViewById(R.id.ivHinhAnh);
        tvTenBaiHat = findViewById(R.id.tvTenBaiHat);
        tvCaSi = findViewById(R.id.tvCaSi);
        tvTheLoai = findViewById(R.id.tvTheLoai);
        tvMoTa = findViewById(R.id.tvMoTa);
        tvThoiGianHienTai = findViewById(R.id.tvThoiGianHienTai);
        tvTongThoiGian = findViewById(R.id.tvTongThoiGian);
        
        btnPhat = findViewById(R.id.btnPhat);
        btnDung = findViewById(R.id.btnDung);
        btnYeuThich = findViewById(R.id.btnYeuThich);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        btnBaiTruoc = findViewById(R.id.btnBaiTruoc);
        btnBaiTiep = findViewById(R.id.btnBaiTiep);
        seekBar = findViewById(R.id.seekBar);

        // Nhận dữ liệu danh sách phát từ MusicPlayerManager
        Intent intent = getIntent();
        if (intent != null) {
            danhSachBaiHat = MusicPlayerManager.getInstance().getPlaylist();
            viTriHienTai = intent.getIntExtra("viTriHienTai", -1);

            if (danhSachBaiHat != null && !danhSachBaiHat.isEmpty() && viTriHienTai >= 0) {
                taiBaiHatHienTai();
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu bài hát", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        btnQuayLai.setOnClickListener(v -> finish());
        btnPhat.setOnClickListener(v -> thayDoiTrangThaiPhat());
        btnDung.setOnClickListener(v -> dungPhatNhac());
        btnYeuThich.setOnClickListener(v -> thayDoiTrangThaiYeuThich());
        
        btnBaiTiep.setOnClickListener(v -> chuyenBaiTiepTheo());
        btnBaiTruoc.setOnClickListener(v -> chuyenBaiTruocDo());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvThoiGianHienTai.setText(dinhDangThoiGian(progress));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void taiBaiHatHienTai() {
        if (danhSachBaiHat == null || danhSachBaiHat.isEmpty() || viTriHienTai < 0 || viTriHienTai >= danhSachBaiHat.size()) {
            return;
        }

        // Lấy bài hát hiện tại từ danh sách
        baiHatHienTai = danhSachBaiHat.get(viTriHienTai);
        if (baiHatHienTai == null) {
            Toast.makeText(this, "Bài hát không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật Giao diện
        tvTenBaiHat.setText(baiHatHienTai.getTenBaiHat() != null ? baiHatHienTai.getTenBaiHat() : "");
        tvCaSi.setText(baiHatHienTai.getCaSi() != null ? baiHatHienTai.getCaSi() : "");
        tvTheLoai.setText(baiHatHienTai.getTheLoai() != null ? baiHatHienTai.getTheLoai() : "");
        tvMoTa.setText(baiHatHienTai.getMoTa() != null ? baiHatHienTai.getMoTa() : "");

        String hinhAnh = baiHatHienTai.getHinhAnh();
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            File file = new File(hinhAnh);
            if (file.exists()) {
                Glide.with(this).load(file).centerCrop().into(ivHinhAnhLinhVat);
            } else {
                ivHinhAnhLinhVat.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            ivHinhAnhLinhVat.setImageResource(R.drawable.ic_launcher_background);
        }

        // Dừng bài cũ (nếu có) trước khi chuẩn bị bài mới
        giaiPhongMediaPlayer();
        
        kiemTraYeuThich();
        chuanBiNhac(baiHatHienTai.getLinkBaiHat());
    }

    private void chuyenBaiTiepTheo() {
        if (danhSachBaiHat == null || danhSachBaiHat.isEmpty()) return;
        viTriHienTai++;
        if (viTriHienTai >= danhSachBaiHat.size()) {
            viTriHienTai = 0; // Quay lại bài đầu tiên nếu đang ở cuối
        }
        taiBaiHatHienTai();
    }

    private void chuyenBaiTruocDo() {
        if (danhSachBaiHat == null || danhSachBaiHat.isEmpty()) return;
        viTriHienTai--;
        if (viTriHienTai < 0) {
            viTriHienTai = danhSachBaiHat.size() - 1; // Nhảy tới bài cuối nếu đang ở đầu
        }
        taiBaiHatHienTai();
    }

    private void kiemTraYeuThich() {
        if (baiHatHienTai == null) return;
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung != -1) {
            daYeuThich = csdlHelper.laYeuThich(idNguoiDung, baiHatHienTai.getId());
            capNhatGiaoDienYeuThich();
        }
    }

    private void thayDoiTrangThaiYeuThich() {
        if (baiHatHienTai == null) return;
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung == -1) return;

        if (daYeuThich) {
            csdlHelper.xoaYeuThich(idNguoiDung, baiHatHienTai.getId());
            Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            // Đảm bảo bài hát (có thể từ Audius) tồn tại trong DB trước khi lưu yêu thích
            csdlHelper.damBaoBaiHatTonTai(baiHatHienTai);
            csdlHelper.themYeuThich(idNguoiDung, baiHatHienTai.getId());
            Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        }
        daYeuThich = !daYeuThich;
        capNhatGiaoDienYeuThich();
    }

    private void capNhatGiaoDienYeuThich() {
        btnYeuThich.setImageResource(daYeuThich ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        btnYeuThich.setColorFilter(ContextCompat.getColor(this, daYeuThich ? R.color.accent_red : R.color.text_secondary));
    }

    private void chuanBiNhac(String duongDanNhac) {
        if (duongDanNhac == null || duongDanNhac.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy file nhạc", Toast.LENGTH_SHORT).show();
            tvThoiGianHienTai.setText("00:00");
            tvTongThoiGian.setText("00:00");
            seekBar.setProgress(0);
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            if (duongDanNhac.startsWith("http")) {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer tcefZoCfV47JRK8A5XSGZFRPI8yZ01Dj8LTFIAlatQA=");
                mediaPlayer.setDataSource(this, android.net.Uri.parse(duongDanNhac), headers);
            } else {
                mediaPlayer.setDataSource(duongDanNhac);
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                tvTongThoiGian.setText(dinhDangThoiGian(mp.getDuration()));
                seekBar.setMax(mp.getDuration());
                thayDoiTrangThaiPhat(); // Tự động phát khi tải xong
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                // Tự động chuyển bài khi kết thúc
                chuyenBaiTiepTheo();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi phát nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    private void thayDoiTrangThaiPhat() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPhat.setImageResource(R.drawable.ic_play);
            dangPhat = false;
        } else {
            mediaPlayer.start();
            btnPhat.setImageResource(R.drawable.ic_pause);
            dangPhat = true;
            capNhatThoiGianThucTe();
        }
    }

    private void dungPhatNhac() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.seekTo(0);
            seekBar.setProgress(0);
            tvThoiGianHienTai.setText("00:00");
            btnPhat.setImageResource(R.drawable.ic_play);
            dangPhat = false;
        }
    }

    private void capNhatThoiGianThucTe() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int thoiGianHienTai = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(thoiGianHienTai);
            tvThoiGianHienTai.setText(dinhDangThoiGian(thoiGianHienTai));
            Runnable runnable = this::capNhatThoiGianThucTe;
            handler.postDelayed(runnable, 1000);
        }
    }

    private String dinhDangThoiGian(int ms) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(ms),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))
        );
    }

    private void giaiPhongMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        dangPhat = false;
        btnPhat.setImageResource(R.drawable.ic_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        giaiPhongMediaPlayer();
    }
}
