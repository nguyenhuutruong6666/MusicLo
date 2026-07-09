package com.example.musiclo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.LuuTruCucBo;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThemSuaBaiHatActivity extends AppCompatActivity {

    ImageButton btnQuayLai;
    TextView tvTenFileNhac;
    EditText edtTenBaiHat, edtCaSi, edtMoTa;
    Spinner spinnerTheLoai;
    ImageView ivChonAnh;
    Button btnChonAnh, btnChonNhac, btnLuuBaiHat;
    ProgressBar progressBar;

    Uri uriAnh = null;
    Uri uriNhac = null;

    int idBaiHat = -1;
    BaiHat baiHatHienTai = null;

    CSDLHelper csdlHelper;
    ExecutorService boThucThi;

    private final String[] DANH_SACH_THE_LOAI = {"Tất cả", "Pop", "Rock", "Ballad", "V-Pop", "R&B", "Rap", "Khác"};

    private final ActivityResultLauncher<Intent> chonAnhLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    uriAnh = result.getData().getData();
                    ivChonAnh.clearColorFilter();
                    ivChonAnh.setImageTintList(null);
                    Glide.with(this).load(uriAnh).centerCrop().into(ivChonAnh);
                }
            }
    );

    private final ActivityResultLauncher<Intent> chonNhacLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    uriNhac = result.getData().getData();
                    tvTenFileNhac.setVisibility(View.VISIBLE);
                    tvTenFileNhac.setText(layTenFileTuUri(uriNhac));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_sua_bai_hat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        boThucThi = Executors.newSingleThreadExecutor();

        btnQuayLai = findViewById(R.id.btnQuayLai);
        tvTenFileNhac = findViewById(R.id.tvTenFileNhac);
        edtTenBaiHat = findViewById(R.id.edtTenBaiHat);
        edtCaSi = findViewById(R.id.edtCaSi);
        edtMoTa = findViewById(R.id.edtMoTa);
        spinnerTheLoai = findViewById(R.id.spinnerTheLoai);

        ArrayAdapter<String> theLoaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DANH_SACH_THE_LOAI);
        theLoaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheLoai.setAdapter(theLoaiAdapter);
        ivChonAnh = findViewById(R.id.ivXemTruocAnh);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnChonNhac = findViewById(R.id.btnChonNhac);
        btnLuuBaiHat = findViewById(R.id.btnLuu);
        progressBar = findViewById(R.id.thanhTienTrinh);

        idBaiHat = getIntent().getIntExtra("idBaiHat", -1);
        if (idBaiHat != -1) {
            taiDuLieuBaiHat(idBaiHat);
        }

        btnQuayLai.setOnClickListener(v -> finish());
        btnChonAnh.setOnClickListener(v -> chonAnh());
        btnChonNhac.setOnClickListener(v -> chonNhac());
        btnLuuBaiHat.setOnClickListener(v -> xuLyLuu());
    }

    private void taiDuLieuBaiHat(int id) {
        baiHatHienTai = csdlHelper.layBaiHatTheoId(id);
        if (baiHatHienTai != null) {
            edtTenBaiHat.setText(baiHatHienTai.getTenBaiHat() != null ? baiHatHienTai.getTenBaiHat() : "");
            edtCaSi.setText(baiHatHienTai.getCaSi() != null ? baiHatHienTai.getCaSi() : "");
            edtMoTa.setText(baiHatHienTai.getMoTa() != null ? baiHatHienTai.getMoTa() : "");

            if (baiHatHienTai.getTheLoai() != null) {
                for (int i = 0; i < DANH_SACH_THE_LOAI.length; i++) {
                    if (DANH_SACH_THE_LOAI[i].equalsIgnoreCase(baiHatHienTai.getTheLoai())) {
                        spinnerTheLoai.setSelection(i);
                        break;
                    }
                }
            }

            if (baiHatHienTai.getHinhAnh() != null && !baiHatHienTai.getHinhAnh().isEmpty()) {
                File fileAnh = new File(baiHatHienTai.getHinhAnh());
                if (fileAnh.exists()) {
                    ivChonAnh.clearColorFilter();
                    ivChonAnh.setImageTintList(null);
                    Glide.with(this).load(fileAnh).centerCrop().into(ivChonAnh);
                }
            }
            if (baiHatHienTai.getLinkBaiHat() != null && !baiHatHienTai.getLinkBaiHat().isEmpty()) {
                tvTenFileNhac.setVisibility(View.VISIBLE);
                tvTenFileNhac.setText(new File(baiHatHienTai.getLinkBaiHat()).getName());
            }
        }
    }

    private void chonAnh() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        chonAnhLauncher.launch(intent);
    }

    private void chonNhac() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        chonNhacLauncher.launch(intent);
    }

    private void xuLyLuu() {
        String tenBaiHat = edtTenBaiHat.getText().toString().trim();
        String caSi = edtCaSi.getText().toString().trim();
        String theLoai = spinnerTheLoai.getSelectedItem().toString();
        String moTa = edtMoTa.getText().toString().trim();

        if (tenBaiHat.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idBaiHat == -1 && uriNhac == null) {
            Toast.makeText(this, "Vui lòng chọn file nhạc", Toast.LENGTH_SHORT).show();
            return;
        }

        hienThiDangTai(true);
        btnLuuBaiHat.setEnabled(false);

        boThucThi.execute(() -> {
            String duongDanAnhMoi = (baiHatHienTai != null) ? baiHatHienTai.getHinhAnh() : null;
            String duongDanNhacMoi = (baiHatHienTai != null) ? baiHatHienTai.getLinkBaiHat() : null;

            if (uriAnh != null) {
                String luuThanhCong = LuuTruCucBo.luuAnhVeMay(this, uriAnh);
                if (luuThanhCong != null) {
                    if (baiHatHienTai != null && baiHatHienTai.getHinhAnh() != null) {
                        LuuTruCucBo.xoaFile(baiHatHienTai.getHinhAnh());
                    }
                    duongDanAnhMoi = luuThanhCong;
                }
            }

            if (uriNhac != null) {
                String luuThanhCong = LuuTruCucBo.luuNhacVeMay(this, uriNhac);
                if (luuThanhCong != null) {
                    if (baiHatHienTai != null && baiHatHienTai.getLinkBaiHat() != null) {
                        LuuTruCucBo.xoaFile(baiHatHienTai.getLinkBaiHat());
                    }
                    duongDanNhacMoi = luuThanhCong;
                }
            }

            String finalAnh = duongDanAnhMoi;
            String finalNhac = duongDanNhacMoi;

            runOnUiThread(() -> {
                boolean thanhCong;
                if (idBaiHat == -1) {
                    long kq = csdlHelper.themBaiHat(tenBaiHat, caSi, theLoai, moTa, finalAnh, finalNhac);
                    thanhCong = kq != -1;
                } else {
                    thanhCong = csdlHelper.capNhatBaiHat(idBaiHat, tenBaiHat, caSi, theLoai, moTa, finalAnh, finalNhac);
                }

                hienThiDangTai(false);
                btnLuuBaiHat.setEnabled(true);

                if (thanhCong) {
                    Toast.makeText(this, "Lưu bài hát thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi lưu bài hát", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void hienThiDangTai(boolean dangTai) {
        progressBar.setVisibility(dangTai ? View.VISIBLE : View.GONE);
    }

    private String layTenFileTuUri(Uri uri) {
        String ketQua = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int chiSoThongTin = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (chiSoThongTin != -1) {
                        ketQua = cursor.getString(chiSoThongTin);
                    }
                }
            }
        }
        if (ketQua == null) {
            ketQua = uri.getPath();
            int cat = ketQua.lastIndexOf('/');
            if (cat != -1) ketQua = ketQua.substring(cat + 1);
        }
        return ketQua;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boThucThi != null && !boThucThi.isShutdown()) {
            boThucThi.shutdown();
        }
    }
}

