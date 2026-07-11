package com.example.musiclo.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;
import com.bumptech.glide.Glide;
import com.example.musiclo.models.BaiHat;

import java.io.File;
import java.util.ArrayList;

public class QuanLyBaiHatAdapter extends ArrayAdapter<BaiHat> {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat;

    public QuanLyBaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
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
        TextView tvTheLoai = customView.findViewById(R.id.tvTheLoai);
        Button btnSua = customView.findViewById(R.id.btnSua);
        Button btnXoa = customView.findViewById(R.id.btnXoa);

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
        
        if (baiHat.getTheLoai() != null) {
            tvTheLoai.setText(baiHat.getTheLoai());
        } else {
            tvTheLoai.setText("");
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

        // Bắt sự kiện khi người dùng nhấn nút Sửa
        btnSua.setTag(baiHat);
        btnSua.setOnClickListener((View.OnClickListener) context);

        btnXoa.setTag(baiHat);
        btnXoa.setOnClickListener((View.OnClickListener) context);
        
        return customView;
    }
}
