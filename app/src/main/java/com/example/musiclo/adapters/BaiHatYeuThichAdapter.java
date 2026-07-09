package com.example.musiclo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.musiclo.DanhSachYeuThichActivity;
import com.example.musiclo.PhatNhacActivity;
import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;

import java.io.File;
import java.util.ArrayList;

public class BaiHatYeuThichAdapter extends ArrayAdapter<BaiHat> {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat;
    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    public BaiHatYeuThichAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.csdlHelper = CSDLHelper.layThucThe(context);
        this.quanLyPhienDangNhap = new QuanLyPhienDangNhap(context);
    }

    @Override
    public int getCount() {
        return this.listBaiHat.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource, null);

        ImageView ivHinhAnh = customView.findViewById(R.id.ivHinhAnh);
        TextView tvTenBaiHat = customView.findViewById(R.id.tvTenBaiHat);
        TextView tvCaSi = customView.findViewById(R.id.tvCaSi);
        ImageButton btnBoYeuThich = customView.findViewById(R.id.btnBoYeuThich);

        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat.getTenBaiHat() != null) {
            tvTenBaiHat.setText(baiHat.getTenBaiHat());
        } else {
            tvTenBaiHat.setText("");
        }

        if (baiHat.getCaSi() != null) {
            tvCaSi.setText(baiHat.getCaSi());
        } else {
            tvCaSi.setText("");
        }

        String duongDanAnh = baiHat.getHinhAnh();
        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            File fileAnh = new File(duongDanAnh);
            if (fileAnh.exists()) {
                Glide.with(context)
                     .load(fileAnh)
                     .placeholder(R.drawable.ic_launcher_background)
                     .centerCrop()
                     .into(ivHinhAnh);
            } else {
                ivHinhAnh.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            ivHinhAnh.setImageResource(R.drawable.ic_launcher_background);
        }

        // Xóa khỏi danh sách yêu thích
        btnBoYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
                if (idNguoiDung != -1) {
                    csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
                    Toast.makeText(context, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                    ((DanhSachYeuThichActivity) context).taiDanhSachYeuThich();
                } else {
                    Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Bắt sự kiện khi người dùng nhấn vào toàn bộ bài hát
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhatNhacActivity.class);
                
                ArrayList<Integer> danhSachId = new ArrayList<>();
                for (BaiHat bh : listBaiHat) {
                    danhSachId.add(bh.getId());
                }
                
                int viTri = listBaiHat.indexOf(baiHat);
                
                intent.putIntegerArrayListExtra("danhSachId", danhSachId);
                intent.putExtra("viTriHienTai", viTri);
                
                context.startActivity(intent);
            }
        });

        return customView;
    }
}
