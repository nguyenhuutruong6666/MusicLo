package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclo.adapters.BaiHatAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DanhSachBaiHatActivity extends AppCompatActivity {

    RecyclerView rvDanhSachBaiHat;
    ProgressBar progressBar;
    EditText edtTimKiem;
    Spinner spinnerTheLoai;
    TextView tvLoiChao;
    BottomNavigationView bottomNav;

    BaiHatAdapter baiHatAdapter;
    List<BaiHat> danhSachBaiHat;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;
    
    private final String[] DANH_SACH_THE_LOAI = {"Tất cả", "Pop", "Rock", "Ballad", "V-Pop", "R&B", "Rap", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_bai_hat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        rvDanhSachBaiHat = findViewById(R.id.rvDanhSachBaiHat);
        progressBar = findViewById(R.id.thanhTienTrinh);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        spinnerTheLoai = findViewById(R.id.spinnerTheLoai);
        tvLoiChao = findViewById(R.id.tvLoiChao);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);

        String hoTen = quanLyPhienDangNhap.layHoTen();
        if (hoTen != null && !hoTen.isEmpty()) {
            tvLoiChao.setText(hoTen + " 👋");
        } else {
            tvLoiChao.setText(quanLyPhienDangNhap.layEmail() + " 👋");
        }

        View ivAnhDaiDien = findViewById(R.id.ivAnhDaiDien);
        if (ivAnhDaiDien != null) ivAnhDaiDien.setVisibility(View.GONE);

        danhSachBaiHat = new ArrayList<>();
        baiHatAdapter = new BaiHatAdapter(this, danhSachBaiHat);
        rvDanhSachBaiHat.setLayoutManager(new LinearLayoutManager(this));
        rvDanhSachBaiHat.setAdapter(baiHatAdapter);
        
        ArrayAdapter<String> theLoaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DANH_SACH_THE_LOAI);
        theLoaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheLoai.setAdapter(theLoaiAdapter);

        baiHatAdapter.datSuKienNhanBaiHat(baiHat -> {
            Intent intent = new Intent(this, PhatNhacActivity.class);
            
            // Tạo danh sách ID từ danh sách đang hiển thị hiện tại
            ArrayList<Integer> danhSachId = new ArrayList<>();
            for (BaiHat bh : danhSachBaiHat) {
                danhSachId.add(bh.getId());
            }
            
            // Lấy vị trí của bài hát vừa click
            int viTri = danhSachBaiHat.indexOf(baiHat);
            
            intent.putIntegerArrayListExtra("danhSachId", danhSachId);
            intent.putExtra("viTriHienTai", viTri);
            
            startActivity(intent);
        });

        baiHatAdapter.datSuKienNhanYeuThich((baiHat, daYeuThich) -> {
            int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
            if (idNguoiDung == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (daYeuThich) {
                csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                csdlHelper.themYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
            taiDanhSachYeuThich();
        });

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String theLoai = (String) spinnerTheLoai.getSelectedItem();
                baiHatAdapter.locDanhSach(s.toString(), theLoai);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerTheLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String theLoai = DANH_SACH_THE_LOAI[position];
                baiHatAdapter.locDanhSach(edtTimKiem.getText().toString(), theLoai);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, DanhSachYeuThichActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, TrangCaNhanActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });

        taiDanhSachBaiHat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachBaiHat();
    }

    private void taiDanhSachBaiHat() {
        progressBar.setVisibility(View.VISIBLE);
        danhSachBaiHat.clear();
        danhSachBaiHat.addAll(csdlHelper.layTatCaBaiHat());
        baiHatAdapter.datDanhSachGoc(danhSachBaiHat);
        taiDanhSachYeuThich();
        progressBar.setVisibility(View.GONE);
    }

    private void taiDanhSachYeuThich() {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung != -1) {
            List<Integer> danhSachId = csdlHelper.layIdYeuThich(idNguoiDung);
            baiHatAdapter.capNhatDanhSachYeuThich(danhSachId);
        }
    }
}

