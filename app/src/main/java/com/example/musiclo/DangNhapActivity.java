package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.models.NguoiDung;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

public class DangNhapActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtMatKhau;
    Button btnDangNhap;
    TextView tvChuyenSangDangKy;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvChuyenSangDangKy = findViewById(R.id.tvChuyenSangDangKy);

        btnDangNhap.setOnClickListener(v -> xuLyDangNhap());
        tvChuyenSangDangKy.setOnClickListener(v ->
                startActivity(new Intent(DangNhapActivity.this, DangKyActivity.class)));
    }

    private void xuLyDangNhap() {
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        NguoiDung nguoiDung = csdlHelper.kiemTraDangNhap(email, matKhau);

        if (nguoiDung == null) {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            return;
        }

        quanLyPhienDangNhap.luuPhien(
                nguoiDung.getId(),
                nguoiDung.getEmail(),
                nguoiDung.getVaiTro(),
                nguoiDung.getHoTen() != null ? nguoiDung.getHoTen() : ""
        );

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

        if ("admin".equals(nguoiDung.getVaiTro())) {
            Intent intent = new Intent(this, TrangQuanTriActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DanhSachBaiHatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }
}

