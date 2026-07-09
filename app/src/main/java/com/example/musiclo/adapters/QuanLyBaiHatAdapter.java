package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;

import java.io.File;
import java.util.List;

public class QuanLyBaiHatAdapter extends RecyclerView.Adapter<QuanLyBaiHatAdapter.HolderQuanLyBaiHat> {

    // Các biến lưu trữ dữ liệu
    private Context context;
    private List<BaiHat> danhSachBaiHat;
    
    // Các biến xử lý sự kiện
    private SuKienSuaBaiHat suKienSua;
    private SuKienXoaBaiHat suKienXoa;
    private SuKienNhanItem suKienNhan;

    // Khai báo giao diện (Interface) để gửi sự kiện ra ngoài
    public interface SuKienNhanItem { 
        void khiNhan(BaiHat baiHat); 
    }
    public interface SuKienSuaBaiHat { 
        void khiNhanSua(BaiHat baiHat); 
    }
    public interface SuKienXoaBaiHat { 
        void khiNhanXoa(BaiHat baiHat); 
    }

    // Hàm khởi tạo (Constructor)
    public QuanLyBaiHatAdapter(Context context, List<BaiHat> danhSachBaiHat) {
        this.context = context;
        this.danhSachBaiHat = danhSachBaiHat;
    }

    // Các hàm để gán sự kiện từ Activity vào Adapter
    public void datSuKienSua(SuKienSuaBaiHat suKien) { 
        this.suKienSua = suKien; 
    }
    public void datSuKienXoa(SuKienXoaBaiHat suKien) { 
        this.suKienXoa = suKien; 
    }
    public void datSuKienNhan(SuKienNhanItem suKien) { 
        this.suKienNhan = suKien; 
    }

    // Khởi tạo một View mới (giao diện cho 1 item)
    @NonNull 
    @Override
    public HolderQuanLyBaiHat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quan_ly_bai_hat, parent, false);
        return new HolderQuanLyBaiHat(view);
    }

    // Đưa dữ liệu của bài hát vào trong giao diện
    @Override
    public void onBindViewHolder(@NonNull HolderQuanLyBaiHat holder, int position) {
        BaiHat baiHat = danhSachBaiHat.get(position);
        
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
        
        // Đặt tên thể loại
        if (baiHat.getTheLoai() != null) {
            holder.tvTheLoai.setText(baiHat.getTheLoai());
        } else {
            holder.tvTheLoai.setText("");
        }

        // Gọi hàm phụ trợ để hiển thị ảnh bìa
        hienThiHinhAnh(baiHat.getHinhAnh(), holder.ivHinhAnh);

        // Bắt sự kiện khi người dùng nhấn nút Sửa
        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienSua != null) {
                    suKienSua.khiNhanSua(baiHat);
                }
            }
        });
        
        // Bắt sự kiện khi người dùng nhấn nút Xóa
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienXoa != null) {
                    suKienXoa.khiNhanXoa(baiHat);
                }
            }
        });
        
        // Bắt sự kiện khi người dùng nhấn vào toàn bộ bài hát
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienNhan != null) {
                    suKienNhan.khiNhan(baiHat);
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

    // Đếm tổng số bài hát
    @Override 
    public int getItemCount() { 
        if (danhSachBaiHat != null) {
            return danhSachBaiHat.size();
        } else {
            return 0;
        }
    }

    // Lớp chứa các thành phần giao diện của một Item
    public static class HolderQuanLyBaiHat extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenBaiHat, tvCaSi, tvTheLoai;
        Button btnSua, btnXoa;

        public HolderQuanLyBaiHat(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần từ XML
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenBaiHat = itemView.findViewById(R.id.tvTenBaiHat);
            tvCaSi = itemView.findViewById(R.id.tvCaSi);
            tvTheLoai = itemView.findViewById(R.id.tvTheLoai);
            btnSua = itemView.findViewById(R.id.btnSua);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
