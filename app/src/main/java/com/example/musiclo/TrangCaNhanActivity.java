package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrangCaNhanActivity extends AppCompatActivity {

    TextView tvHoTen;
    TextView tvEmail;
    TextView tvVaiTro;
    Button btnDangXuat, btnGuiPhanHoi;
    BottomNavigationView bottomNav;

    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_ca_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        tvHoTen = findViewById(R.id.tvHoTen);
        tvEmail = findViewById(R.id.tvEmail);
        tvVaiTro = findViewById(R.id.tvVaiTro);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnGuiPhanHoi = findViewById(R.id.btnGuiPhanHoi);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);

        taiThongTinNguoiDung();

        btnDangXuat.setOnClickListener(v -> hienThiXacNhanDangXuat());
        btnGuiPhanHoi.setOnClickListener(v -> startActivity(new Intent(this, GuiPhanHoiActivity.class)));

        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile)
                return true;
            else if (id == R.id.nav_home) {
                startActivity(new Intent(this, DanhSachBaiHatActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, DanhSachYeuThichActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void taiThongTinNguoiDung() {
        String hoTen = quanLyPhienDangNhap.layHoTen();
        String email = quanLyPhienDangNhap.layEmail();
        String vaiTro = quanLyPhienDangNhap.layVaiTro();

        String hienThiTen = (hoTen != null && !hoTen.isEmpty()) ? hoTen : email;
        tvHoTen.setText(hienThiTen != null ? hienThiTen : "Chưa cập nhật");
        tvEmail.setText((email != null && !email.isEmpty()) ? email : "Chưa cập nhật");
        tvVaiTro.setText("Quyền: " + ("admin".equals(vaiTro) ? "Quản trị viên" : "Người dùng"));
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
