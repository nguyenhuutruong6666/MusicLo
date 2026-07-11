package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.adapters.BaiHatAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class DanhSachBaiHatActivity extends AppCompatActivity implements View.OnClickListener {

    ListView rvDanhSachBaiHat;
    EditText edtTimKiem;
    Spinner spinnerTheLoai;
    TextView tvTrong;
    BottomNavigationView bottomNav;

    BaiHatAdapter baiHatAdapter;
    ArrayList<BaiHat> danhSachGoc;
    ArrayList<BaiHat> danhSachHienThi;
    ArrayList<Integer> danhSachIdYeuThich;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    String tuKhoaTimKiem = "";
    String theLoaiHienTai = "Tất cả";
    String[] DANH_SACH_THE_LOAI = {"Tất cả", "Pop", "Rock", "Ballad", "V-Pop", "R&B", "Rap", "Khác"};

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
        edtTimKiem = findViewById(R.id.edtTimKiem);
        spinnerTheLoai = findViewById(R.id.spinnerTheLoai);
        tvTrong = findViewById(R.id.tvTrong);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);

        danhSachGoc = new ArrayList<>();
        danhSachHienThi = new ArrayList<>();
        danhSachIdYeuThich = new ArrayList<>();
        
        baiHatAdapter = new BaiHatAdapter(this, R.layout.item_bai_hat, danhSachHienThi, danhSachIdYeuThich);
        rvDanhSachBaiHat.setAdapter(baiHatAdapter);

        rvDanhSachBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DanhSachBaiHatActivity.this, PhatNhacActivity.class);
                
                ArrayList<Integer> danhSachId = new ArrayList<>();
                for (BaiHat bh : danhSachHienThi) {
                    danhSachId.add(bh.getId());
                }
                
                intent.putIntegerArrayListExtra("danhSachId", danhSachId);
                intent.putExtra("viTriHienTai", position);
                
                startActivity(intent);
            }
        });
        
        ArrayAdapter<String> theLoaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DANH_SACH_THE_LOAI);
        theLoaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheLoai.setAdapter(theLoaiAdapter);

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tuKhoaTimKiem = s.toString();
                locDanhSach();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerTheLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theLoaiHienTai = DANH_SACH_THE_LOAI[position];
                locDanhSach();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachGoc();
        taiDanhSachYeuThich();
    }

    private void taiDanhSachGoc() {
        danhSachGoc.clear();
        danhSachGoc.addAll(csdlHelper.layTatCaBaiHat());
        locDanhSach();
    }

    public void taiDanhSachYeuThich() {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung != -1) {
            danhSachIdYeuThich.clear();
            ArrayList<BaiHat> yeuThich = (ArrayList<BaiHat>) csdlHelper.layDanhSachYeuThich(idNguoiDung);
            for (BaiHat bh : yeuThich) {
                danhSachIdYeuThich.add(bh.getId());
            }
            baiHatAdapter.notifyDataSetChanged();
        }
    }

    private void locDanhSach() {
        danhSachHienThi.clear();
        
        String tuKhoa = tuKhoaTimKiem != null ? tuKhoaTimKiem.trim().toLowerCase() : "";
        
        for (BaiHat baiHat : danhSachGoc) {
            boolean phuHopTuKhoa = false;
            if (tuKhoa.isEmpty()) {
                phuHopTuKhoa = true;
            } else {
                String tenBaiHat = baiHat.getTenBaiHat();
                String caSi = baiHat.getCaSi();
                if (tenBaiHat != null && tenBaiHat.toLowerCase().contains(tuKhoa)) {
                    phuHopTuKhoa = true;
                } else if (caSi != null && caSi.toLowerCase().contains(tuKhoa)) {
                    phuHopTuKhoa = true;
                }
            }

            boolean phuHopTheLoai = false;
            if (theLoaiHienTai.equals("Tất cả")) {
                phuHopTheLoai = true;
            } else {
                String theLoaiBaiHat = baiHat.getTheLoai();
                if (theLoaiBaiHat != null && theLoaiBaiHat.equalsIgnoreCase(theLoaiHienTai)) {
                    phuHopTheLoai = true;
                }
            }

            if (phuHopTuKhoa && phuHopTheLoai) {
                danhSachHienThi.add(baiHat);
            }
        }
        
        baiHatAdapter.notifyDataSetChanged();
        
        if (danhSachHienThi.isEmpty()) {
            tvTrong.setVisibility(View.VISIBLE);
            rvDanhSachBaiHat.setVisibility(View.GONE);
        } else {
            tvTrong.setVisibility(View.GONE);
            rvDanhSachBaiHat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnYeuThich) {
            BaiHat baiHat = (BaiHat) v.getTag();
            int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
            if (idNguoiDung == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean hienTaiDaYeuThich = danhSachIdYeuThich.contains(baiHat.getId());
            if (hienTaiDaYeuThich) {
                csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                csdlHelper.themYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
            taiDanhSachYeuThich();
        }
    }
}
