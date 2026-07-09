package com.example.musiclo;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.adapters.NguoiDungAdapter;
import com.example.musiclo.models.NguoiDung;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

import java.util.ArrayList;

public class QuanLyNguoiDungActivity extends AppCompatActivity {

    ListView rvDanhSachNguoiDung;
    ImageButton btnQuayLai;

    NguoiDungAdapter adapter;
    ArrayList<NguoiDung> danhSachNguoiDung;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_nguoi_dung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        rvDanhSachNguoiDung = findViewById(R.id.rvDanhSachNguoiDung);
        btnQuayLai = findViewById(R.id.btnQuayLai);

        btnQuayLai.setOnClickListener(v -> finish());

        danhSachNguoiDung = new ArrayList<>();
        adapter = new NguoiDungAdapter(this, R.layout.item_nguoi_dung, danhSachNguoiDung);
        rvDanhSachNguoiDung.setAdapter(adapter);

        taiDanhSachNguoiDung();
    }

    public void taiDanhSachNguoiDung() {
        danhSachNguoiDung.clear();
        danhSachNguoiDung.addAll(csdlHelper.layTatCaNguoiDung());
        adapter.notifyDataSetChanged();
    }
}
