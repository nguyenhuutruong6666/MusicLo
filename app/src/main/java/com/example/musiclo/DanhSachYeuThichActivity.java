package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.adapters.BaiHatYeuThichAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;

public class DanhSachYeuThichActivity extends AppCompatActivity implements View.OnClickListener {

    ListView rvDanhSachYeuThich;
    TextView tvTrong;
    BottomNavigationView bottomNav;

    BaiHatYeuThichAdapter adapter;
    ArrayList<BaiHat> danhSachYeuThich;

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
        tvTrong = findViewById(R.id.tvTrong);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);

        danhSachYeuThich = new ArrayList<>();
        adapter = new BaiHatYeuThichAdapter(this, R.layout.item_bai_hat_yeu_thich, danhSachYeuThich);
        rvDanhSachYeuThich.setAdapter(adapter);

        rvDanhSachYeuThich.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DanhSachYeuThichActivity.this, PhatNhacActivity.class);
                
                ArrayList<Integer> danhSachId = new ArrayList<>();
                for (BaiHat bh : danhSachYeuThich) {
                    danhSachId.add(bh.getId());
                }
                
                intent.putIntegerArrayListExtra("danhSachId", danhSachId);
                intent.putExtra("viTriHienTai", position);
                
                startActivity(intent);
            }
        });

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachYeuThich();
    }

    public void taiDanhSachYeuThich() {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        danhSachYeuThich.clear();
        danhSachYeuThich.addAll(csdlHelper.layDanhSachYeuThich(idNguoiDung));
        adapter.notifyDataSetChanged();

        if (danhSachYeuThich.isEmpty()) {
            tvTrong.setVisibility(View.VISIBLE);
            rvDanhSachYeuThich.setVisibility(View.GONE);
        } else {
            tvTrong.setVisibility(View.GONE);
            rvDanhSachYeuThich.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBoYeuThich) {
            BaiHat baiHat = (BaiHat) v.getTag();
            int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
            if (idNguoiDung != -1) {
                csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                taiDanhSachYeuThich();
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
