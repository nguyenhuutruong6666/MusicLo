package com.example.musiclo;

import android.os.Bundle;
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

import com.example.musiclo.adapters.NguoiDungAdapter;
import com.example.musiclo.models.NguoiDung;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

import java.util.ArrayList;
import java.util.List;

public class QuanLyNguoiDungActivity extends AppCompatActivity {

    RecyclerView rvDanhSachNguoiDung;
    ImageButton btnQuayLai;

    NguoiDungAdapter adapter;
    List<NguoiDung> danhSachNguoiDung;

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
        adapter = new NguoiDungAdapter(this, danhSachNguoiDung);
        rvDanhSachNguoiDung.setLayoutManager(new LinearLayoutManager(this));
        rvDanhSachNguoiDung.setAdapter(adapter);

        adapter.datSuKienDoiVaiTro(this::hienThiXacNhanDoiVaiTro);
        adapter.datSuKienXoaNguoiDung(this::hienThiXacNhanXoa);

        taiDanhSachNguoiDung();
    }

    private void taiDanhSachNguoiDung() {
        danhSachNguoiDung.clear();
        danhSachNguoiDung.addAll(csdlHelper.layTatCaNguoiDung());
        adapter.notifyDataSetChanged();
    }

    private void hienThiXacNhanDoiVaiTro(NguoiDung nguoiDung, String vaiTroMoi) {
        String tenVaiTroMoi = "admin".equals(vaiTroMoi) ? "Admin" : "User";
        new AlertDialog.Builder(this)
                .setTitle("Đổi quyền")
                .setMessage("Bạn có muốn đổi quyền của \"" + nguoiDung.getEmail() + "\" thành " + tenVaiTroMoi + " không?")
                .setPositiveButton("Đồng ý", (d, w) -> {
                    boolean thanhCong = csdlHelper.capNhatVaiTro(nguoiDung.getId(), vaiTroMoi);
                    if (thanhCong) {
                        Toast.makeText(this, "Đã đổi quyền thành " + vaiTroMoi, Toast.LENGTH_SHORT).show();
                        taiDanhSachNguoiDung();
                    } else {
                        Toast.makeText(this, "Lỗi đổi quyền", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void hienThiXacNhanXoa(NguoiDung nguoiDung) {
        if (nguoiDung.getId() == quanLyPhienDangNhap.layIdNguoiDung()) {
            Toast.makeText(this, "Không thể xóa tài khoản đang đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có muốn xóa người dùng \"" + nguoiDung.getEmail() + "\" không?")
                .setPositiveButton("Xóa", (d, w) -> {
                    boolean thanhCong = csdlHelper.xoaNguoiDung(nguoiDung.getId());
                    if (thanhCong) {
                        Toast.makeText(this, "Đã xóa người dùng \"" + nguoiDung.getEmail() + "\"", Toast.LENGTH_SHORT).show();
                        taiDanhSachNguoiDung();
                    } else {
                        Toast.makeText(this, "Lỗi xóa người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
