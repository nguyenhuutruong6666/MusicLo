package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;

import java.io.File;
import java.util.List;

public class BaiHatYeuThichAdapter extends RecyclerView.Adapter<BaiHatYeuThichAdapter.HolderBaiHatYeuThich> {

    // Các biến lưu trữ dữ liệu
    private Context context;
    private List<BaiHat> danhSachYeuThich;
    
    // Các biến xử lý sự kiện
    private SuKienNhanItem suKienNhan;
    private SuKienBoYeuThich suKienBoYeuThich;

    // Khai báo giao diện (Interface) để gửi sự kiện ra ngoài
    public interface SuKienNhanItem { 
        void khiNhan(BaiHat baiHat); 
    }
    public interface SuKienBoYeuThich { 
        void khiBoYeuThich(BaiHat baiHat); 
    }

    // Hàm khởi tạo (Constructor)
    public BaiHatYeuThichAdapter(Context context, List<BaiHat> danhSachYeuThich) {
        this.context = context;
        this.danhSachYeuThich = danhSachYeuThich;
    }

    // Các hàm để gán sự kiện từ Activity vào Adapter
    public void datSuKienNhan(SuKienNhanItem suKien) { 
        this.suKienNhan = suKien; 
    }
    public void datSuKienBoYeuThich(SuKienBoYeuThich suKien) { 
        this.suKienBoYeuThich = suKien; 
    }

    // Khởi tạo một View mới (giao diện cho 1 item)
    @NonNull 
    @Override
    public HolderBaiHatYeuThich onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bai_hat_yeu_thich, parent, false);
        return new HolderBaiHatYeuThich(view);
    }

    // Đưa dữ liệu của bài hát vào trong giao diện
    @Override
    public void onBindViewHolder(@NonNull HolderBaiHatYeuThich holder, int position) {
        BaiHat baiHat = danhSachYeuThich.get(position);
        
        // Đặt tên bài hát
        if (baiHat.getTenBaiHat() != null) {
            holder.tvTenBaiHat.setText(baiHat.getTenBaiHat());
        } else {
            holder.tvTenBaiHat.setText("");
        }

        // Đặt tên ca sĩ
        if (baiHat.getCaSi() != null) {
            holder.tvCaSi.setText(baiHat.getCaSi());
        } else {
            holder.tvCaSi.setText("");
        }

        // Gọi hàm phụ trợ để hiển thị ảnh bìa
        hienThiHinhAnh(baiHat.getHinhAnh(), holder.ivHinhAnh);

        // Bắt sự kiện khi người dùng nhấn vào toàn bộ bài hát
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienNhan != null) {
                    suKienNhan.khiNhan(baiHat);
                }
            }
        });
        
        // Bắt sự kiện khi người dùng nhấn nút Hủy yêu thích
        holder.btnBoYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienBoYeuThich != null) {
                    suKienBoYeuThich.khiBoYeuThich(baiHat);
                }
            }
        });
    }
    
    // Hàm phụ trợ giúp tải và hiển thị hình ảnh một cách dễ hiểu
    private void hienThiHinhAnh(String duongDanAnh, ImageView ivHinhAnh) {
        if (duongDanAnh == null || duongDanAnh.isEmpty()) {
            // Nếu không có ảnh, hiển thị ảnh mặc định
            ivHinhAnh.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        File fileAnh = new File(duongDanAnh);
        if (fileAnh.exists()) {
            // Nếu file ảnh thực sự tồn tại trong máy, dùng Glide để hiển thị
            Glide.with(context)
                 .load(fileAnh)
                 .placeholder(R.drawable.ic_launcher_background)
                 .centerCrop()
                 .into(ivHinhAnh);
        } else {
            // Nếu đường dẫn lỗi, hiển thị ảnh mặc định
            ivHinhAnh.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    // Đếm tổng số bài hát trong danh sách yêu thích
    @Override 
    public int getItemCount() { 
        if (danhSachYeuThich != null) {
            return danhSachYeuThich.size();
        } else {
            return 0;
        }
    }

    // Lớp chứa các thành phần giao diện của một Item
    public static class HolderBaiHatYeuThich extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenBaiHat, tvCaSi;
        ImageButton btnBoYeuThich;

        public HolderBaiHatYeuThich(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần từ XML
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenBaiHat = itemView.findViewById(R.id.tvTenBaiHat);
            tvCaSi = itemView.findViewById(R.id.tvCaSi);
            btnBoYeuThich = itemView.findViewById(R.id.btnBoYeuThich);
        }
    }
}
