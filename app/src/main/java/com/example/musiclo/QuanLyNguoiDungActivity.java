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

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.view.View;

public class QuanLyNguoiDungActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    public void onClick(View v) {
        NguoiDung nguoiDung = (NguoiDung) v.getTag();
        if (v.getId() == R.id.btnDoiVaiTro) {
            String vaiTroMoi = "admin".equals(nguoiDung.getVaiTro()) ? "user" : "admin";
            String tenVaiTroMoi = "admin".equals(vaiTroMoi) ? "Admin" : "User";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Đổi quyền");
            builder.setMessage(
                    "Bạn có muốn đổi quyền của \"" + nguoiDung.getEmail() + "\" thành " + tenVaiTroMoi + " không?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    boolean thanhCong = csdlHelper.capNhatVaiTro(nguoiDung.getId(), vaiTroMoi);
                    if (thanhCong) {
                        Toast.makeText(QuanLyNguoiDungActivity.this, "Đã đổi quyền thành " + vaiTroMoi, Toast.LENGTH_SHORT).show();
                        taiDanhSachNguoiDung();
                        dialogInterface.dismiss();
                    } else {
                        Toast.makeText(QuanLyNguoiDungActivity.this, "Lỗi đổi quyền", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        } else if (v.getId() == R.id.btnXoa) {
            if (nguoiDung.getId() == quanLyPhienDangNhap.layIdNguoiDung()) {
                Toast.makeText(this, "Không thể xóa tài khoản đang đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có muốn xóa người dùng \"" + nguoiDung.getEmail() + "\" không?");
            builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    boolean thanhCong = csdlHelper.xoaNguoiDung(nguoiDung.getId());
                    if (thanhCong) {
                        Toast.makeText(QuanLyNguoiDungActivity.this, "Đã xóa người dùng \"" + nguoiDung.getEmail() + "\"",
                                Toast.LENGTH_SHORT).show();
                        taiDanhSachNguoiDung();
                        dialogInterface.dismiss();
                    } else {
                        Toast.makeText(QuanLyNguoiDungActivity.this, "Lỗi xóa người dùng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }
}
