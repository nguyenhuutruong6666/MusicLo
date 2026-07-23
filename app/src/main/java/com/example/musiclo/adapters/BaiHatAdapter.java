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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;

import java.io.File;
import java.util.ArrayList;

public class BaiHatAdapter extends ArrayAdapter<BaiHat> {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat;
    ArrayList<Integer> danhSachIdYeuThich;

    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, ArrayList<Integer> danhSachIdYeuThich) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.danhSachIdYeuThich = danhSachIdYeuThich;
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
        ImageButton btnYeuThich = customView.findViewById(R.id.btnYeuThich);

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

        boolean daYeuThich = false;
        if (danhSachIdYeuThich.contains(baiHat.getId())) {
            daYeuThich = true;
        }

        if (daYeuThich) {
            btnYeuThich.setImageResource(R.drawable.ic_favorite_filled);
            btnYeuThich.setColorFilter(ContextCompat.getColor(context, R.color.accent_red));
        } else {
            btnYeuThich.setImageResource(R.drawable.ic_favorite_border);
            btnYeuThich.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary));
        }

        btnYeuThich.setTag(baiHat);
        btnYeuThich.setOnClickListener((View.OnClickListener) context);

        return customView;
    }
}
