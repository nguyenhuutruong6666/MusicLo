package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclo.adapters.BaiHatYeuThichAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DanhSachYeuThichActivity extends AppCompatActivity {

    RecyclerView rvDanhSachYeuThich;
    ProgressBar progressBar;
    TextView tvTrong;
    BottomNavigationView bottomNav;

    BaiHatYeuThichAdapter adapter;
    List<BaiHat> danhSachYeuThich;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_yeu_thich);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        rvDanhSachYeuThich = findViewById(R.id.rvDanhSachYeuThich);
        progressBar = findViewById(R.id.thanhTienTrinh);
        tvTrong = findViewById(R.id.tvTrong);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);

        danhSachYeuThich = new ArrayList<>();
        adapter = new BaiHatYeuThichAdapter(this, danhSachYeuThich);
        rvDanhSachYeuThich.setLayoutManager(new LinearLayoutManager(this));
        rvDanhSachYeuThich.setAdapter(adapter);

        adapter.datSuKienNhan(baiHat -> {
            Intent intent = new Intent(this, PhatNhacActivity.class);
            
            // Tạo danh sách ID từ danh sách yêu thích
            ArrayList<Integer> danhSachId = new ArrayList<>();
            for (BaiHat bh : danhSachYeuThich) {
                danhSachId.add(bh.getId());
            }
            
            // Lấy vị trí của bài hát vừa click
            int viTri = danhSachYeuThich.indexOf(baiHat);
            
            intent.putIntegerArrayListExtra("danhSachId", danhSachId);
            intent.putExtra("viTriHienTai", viTri);
            
            startActivity(intent);
        });

        adapter.datSuKienBoYeuThich(baiHat ->
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận bỏ yêu thích")
                        .setMessage("Bạn có chắc chắn muốn bỏ bài hát này?")
                        .setPositiveButton("Bỏ", (d, w) -> xoaYeuThich(baiHat))
                        .setNegativeButton("Hủy", null)
                        .show()
        );

        bottomNav.setSelectedItemId(R.id.nav_favorites);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_favorites) return true;
            else if (id == R.id.nav_home) {
                startActivity(new Intent(this, DanhSachBaiHatActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, TrangCaNhanActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });

        taiDanhSachYeuThich();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachYeuThich();
    }

    private void taiDanhSachYeuThich() {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        danhSachYeuThich.clear();
        danhSachYeuThich.addAll(csdlHelper.layDanhSachYeuThich(idNguoiDung));
        progressBar.setVisibility(View.GONE);
        tvTrong.setVisibility(danhSachYeuThich.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void xoaYeuThich(BaiHat baiHat) {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung == -1) return;
        csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
        Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
        taiDanhSachYeuThich();
    }
}

