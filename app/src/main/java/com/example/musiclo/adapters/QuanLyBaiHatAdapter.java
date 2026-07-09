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

import com.bumptech.glide.Glide;
import com.example.musiclo.PhatNhacActivity;
import com.example.musiclo.QuanLyBaiHatActivity;
import com.example.musiclo.R;
import com.example.musiclo.ThemSuaBaiHatActivity;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;

import java.io.File;
import java.util.ArrayList;

public class QuanLyBaiHatAdapter extends ArrayAdapter<BaiHat> {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat;
    CSDLHelper csdlHelper;

    public QuanLyBaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.csdlHelper = CSDLHelper.layThucThe(context);
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
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThemSuaBaiHatActivity.class);
                intent.putExtra("idBaiHat", baiHat.getId());
                context.startActivity(intent);
            }
        });
        
        // Bắt sự kiện khi người dùng nhấn nút Xóa
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa bài hát này không?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            boolean thanhCong = csdlHelper.xoaBaiHat(baiHat.getId());
                            if (thanhCong) {
                                Toast.makeText(context, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                                ((QuanLyBaiHatActivity) context).taiDanhSachBaiHat();
                                dialogInterface.dismiss();
                            } else {
                                Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Lỗi xóa dữ liệu", Toast.LENGTH_SHORT).show();
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
