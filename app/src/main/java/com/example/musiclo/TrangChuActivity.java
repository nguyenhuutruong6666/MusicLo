package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.utils.QuanLyPhienDangNhap;

public class TrangChuActivity extends AppCompatActivity {

    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        if (!quanLyPhienDangNhap.daDangNhap()) {
            chuyenSangDangNhap();
            return;
        }

        String vaiTro = quanLyPhienDangNhap.layVaiTro();
        if ("admin".equals(vaiTro)) {
            chuyenSangTrangQuanTri();
        } else {
            chuyenSangDanhSachBaiHat();
        }
    }

    private void chuyenSangDangNhap() {
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void chuyenSangDanhSachBaiHat() {
        Intent intent = new Intent(this, DanhSachBaiHatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void chuyenSangTrangQuanTri() {
        Intent intent = new Intent(this, TrangQuanTriActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
