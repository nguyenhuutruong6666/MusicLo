package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.adapters.QuanLyBaiHatAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;

import java.util.ArrayList;

public class QuanLyBaiHatActivity extends AppCompatActivity {

    ListView rvDanhSachBaiHat;
    Button btnThem;
    ImageButton btnQuayLai;

    QuanLyBaiHatAdapter adapter;
    ArrayList<BaiHat> danhSachBaiHat;
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
        adapter = new QuanLyBaiHatAdapter(this, R.layout.item_quan_ly_bai_hat, danhSachBaiHat);
        rvDanhSachBaiHat.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachBaiHat();
    }

    public void taiDanhSachBaiHat() {
        danhSachBaiHat.clear();
        danhSachBaiHat.addAll(csdlHelper.layTatCaBaiHat());
        adapter.notifyDataSetChanged();
    }
}
