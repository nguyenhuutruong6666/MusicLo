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

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.AdapterView;
import com.example.musiclo.utils.MusicPlayerManager;

public class QuanLyBaiHatActivity extends AppCompatActivity implements View.OnClickListener {

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

        rvDanhSachBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayerManager.getInstance().setPlaylist(danhSachBaiHat);
                
                Intent intent = new Intent(QuanLyBaiHatActivity.this, PhatNhacActivity.class);
                intent.putExtra("viTriHienTai", position);
                startActivity(intent);
            }
        });
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

    @Override
    public void onClick(View v) {
        BaiHat baiHat = (BaiHat) v.getTag();
        if (v.getId() == R.id.btnSua) {
            Intent intent = new Intent(this, ThemSuaBaiHatActivity.class);
            intent.putExtra("idBaiHat", baiHat.getId());
            startActivity(intent);
        } else if (v.getId() == R.id.btnXoa) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc chắn muốn xóa bài hát này không?");
            builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        boolean thanhCong = csdlHelper.xoaBaiHat(baiHat.getId());
                        if (thanhCong) {
                            Toast.makeText(QuanLyBaiHatActivity.this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                            taiDanhSachBaiHat();
                            dialogInterface.dismiss();
                        } else {
                            Toast.makeText(QuanLyBaiHatActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(QuanLyBaiHatActivity.this, "Lỗi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }
}
