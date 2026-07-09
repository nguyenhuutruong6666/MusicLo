package com.example.musiclo.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musiclo.QuanLyNguoiDungActivity;
import com.example.musiclo.R;
import com.example.musiclo.models.NguoiDung;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

import java.util.ArrayList;

public class NguoiDungAdapter extends ArrayAdapter<NguoiDung> {
    Activity context;
    int resource;
    ArrayList<NguoiDung> listNguoiDung;
    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    public NguoiDungAdapter(Activity context, int resource, ArrayList<NguoiDung> listNguoiDung) {
        super(context, resource, listNguoiDung);
        this.context = context;
        this.resource = resource;
        this.listNguoiDung = listNguoiDung;
        this.csdlHelper = CSDLHelper.layThucThe(context);
        this.quanLyPhienDangNhap = new QuanLyPhienDangNhap(context);
    }

    @Override
    public int getCount() {
        return this.listNguoiDung.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource, null);

        TextView tvHoTen = customView.findViewById(R.id.tvHoTen);
        TextView tvEmail = customView.findViewById(R.id.tvEmail);
        TextView tvVaiTro = customView.findViewById(R.id.tvVaiTro);
        Button btnDoiVaiTro = customView.findViewById(R.id.btnDoiVaiTro);
        Button btnXoa = customView.findViewById(R.id.btnXoa);

        NguoiDung nguoiDung = listNguoiDung.get(position);
        
        if (nguoiDung.getEmail() != null) {
            tvEmail.setText(nguoiDung.getEmail());
        } else {
            tvEmail.setText("");
        }

        String hoTen = nguoiDung.getHoTen();
        if (hoTen != null && !hoTen.isEmpty()) {
            tvHoTen.setText(hoTen);
        } else {
            tvHoTen.setText("Chưa cập nhật");
        }

        String vaiTroHienTai = nguoiDung.getVaiTro();
        if ("admin".equals(vaiTroHienTai)) {
            tvVaiTro.setText("Vai trò: Admin");
            btnDoiVaiTro.setText("Đổi thành User");
        } else {
            tvVaiTro.setText("Vai trò: Người dùng");
            btnDoiVaiTro.setText("Đổi thành Admin");
        }

        btnDoiVaiTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vaiTroMoi = "admin".equals(nguoiDung.getVaiTro()) ? "user" : "admin";
                String tenVaiTroMoi = "admin".equals(vaiTroMoi) ? "Admin" : "User";

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Đổi quyền");
                builder.setMessage("Bạn có muốn đổi quyền của \"" + nguoiDung.getEmail() + "\" thành " + tenVaiTroMoi + " không?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean thanhCong = csdlHelper.capNhatVaiTro(nguoiDung.getId(), vaiTroMoi);
                        if (thanhCong) {
                            Toast.makeText(context, "Đã đổi quyền thành " + vaiTroMoi, Toast.LENGTH_SHORT).show();
                            ((QuanLyNguoiDungActivity) context).taiDanhSachNguoiDung();
                            dialogInterface.dismiss();
                        } else {
                            Toast.makeText(context, "Lỗi đổi quyền", Toast.LENGTH_SHORT).show();
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
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nguoiDung.getId() == quanLyPhienDangNhap.layIdNguoiDung()) {
                    Toast.makeText(context, "Không thể xóa tài khoản đang đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có muốn xóa người dùng \"" + nguoiDung.getEmail() + "\" không?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean thanhCong = csdlHelper.xoaNguoiDung(nguoiDung.getId());
                        if (thanhCong) {
                            Toast.makeText(context, "Đã xóa người dùng \"" + nguoiDung.getEmail() + "\"", Toast.LENGTH_SHORT).show();
                            ((QuanLyNguoiDungActivity) context).taiDanhSachNguoiDung();
                            dialogInterface.dismiss();
                        } else {
                            Toast.makeText(context, "Lỗi xóa người dùng", Toast.LENGTH_SHORT).show();
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
        });

        return customView;
    }
}
