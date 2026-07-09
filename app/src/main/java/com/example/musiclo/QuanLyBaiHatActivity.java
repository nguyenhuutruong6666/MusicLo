package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclo.adapters.QuanLyBaiHatAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.LuuTruCucBo;

import java.util.ArrayList;
import java.util.List;

public class QuanLyBaiHatActivity extends AppCompatActivity {

    RecyclerView rvDanhSachBaiHat;
    Button btnThem;
    ImageButton btnQuayLai;

    QuanLyBaiHatAdapter adapter;
    List<BaiHat> danhSachBaiHat;
    CSDLHelper csdlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_bai_hat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);

        rvDanhSachBaiHat = findViewById(R.id.rvDanhSachBaiHat);
        btnThem = findViewById(R.id.btnThemBaiHat);
        btnQuayLai = findViewById(R.id.btnQuayLai);

        btnQuayLai.setOnClickListener(v -> finish());
        btnThem.setOnClickListener(v -> startActivity(new Intent(this, ThemSuaBaiHatActivity.class)));

        danhSachBaiHat = new ArrayList<>();
        adapter = new QuanLyBaiHatAdapter(this, danhSachBaiHat);
        rvDanhSachBaiHat.setLayoutManager(new LinearLayoutManager(this));
        rvDanhSachBaiHat.setAdapter(adapter);

        adapter.datSuKienNhan(baiHat -> {
            Intent intent = new Intent(this, PhatNhacActivity.class);
            
            // Tạo danh sách ID từ danh sách quản lý
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

        adapter.datSuKienSua(baiHat -> {
            Intent intent = new Intent(this, ThemSuaBaiHatActivity.class);
            intent.putExtra("idBaiHat", baiHat.getId());
            startActivity(intent);
        });

        adapter.datSuKienXoa(this::hienThiXacNhanXoa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachBaiHat();
    }

    private void taiDanhSachBaiHat() {
        danhSachBaiHat.clear();
        danhSachBaiHat.addAll(csdlHelper.layTatCaBaiHat());
        adapter.notifyDataSetChanged();
    }

    private void hienThiXacNhanXoa(BaiHat baiHat) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài hát \"" + baiHat.getTenBaiHat() + "\"")
                .setPositiveButton("Xóa", (d, w) -> thucHienXoaBaiHat(baiHat))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void thucHienXoaBaiHat(BaiHat baiHat) {
        LuuTruCucBo.xoaFile(baiHat.getHinhAnh());
        LuuTruCucBo.xoaFile(baiHat.getLinkBaiHat());

        boolean thanhCong = csdlHelper.xoaBaiHat(baiHat.getId());
        if (thanhCong) {
            Toast.makeText(this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
            taiDanhSachBaiHat();
        } else {
            Toast.makeText(this, "Lỗi khi xóa bài hát", Toast.LENGTH_SHORT).show();
        }
    }
}

