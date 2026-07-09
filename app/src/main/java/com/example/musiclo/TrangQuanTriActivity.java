package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.utils.QuanLyPhienDangNhap;

public class TrangQuanTriActivity extends AppCompatActivity {

    Button btnQuanLyNhac, btnQuanLyNguoiDung, btnDangXuat;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_quan_tri);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        btnQuanLyNhac = findViewById(R.id.btnQuanLyNhac);
        btnQuanLyNguoiDung = findViewById(R.id.btnQuanLyNguoiDung);
        btnDangXuat = findViewById(R.id.btnDangXuat);

        btnQuanLyNhac.setOnClickListener(v -> startActivity(new Intent(this, QuanLyBaiHatActivity.class)));
        btnQuanLyNguoiDung.setOnClickListener(v -> startActivity(new Intent(this, QuanLyNguoiDungActivity.class)));
        btnDangXuat.setOnClickListener(v -> hienThiXacNhanDangXuat());
    }

    private void hienThiXacNhanDangXuat() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (d, w) -> xuLyDangXuat())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void xuLyDangXuat() {
        quanLyPhienDangNhap.xoaPhien();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

