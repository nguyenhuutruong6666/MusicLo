package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musiclo.R;
import com.example.musiclo.models.BaiHat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaiHatAdapter extends RecyclerView.Adapter<BaiHatAdapter.HolderBaiHat> {

    // Các biến lưu trữ dữ liệu
    private Context context;
    private List<BaiHat> danhSachBaiHat;
    private List<BaiHat> danhSachGoc;
    private List<Integer> danhSachIdYeuThich;

    // Các biến xử lý sự kiện
    private SuKienNhanBaiHat suKienNhanBaiHat;
    private SuKienNhanYeuThich suKienNhanYeuThich;

    // Các biến dùng để lọc danh sách
    private String tuKhoaTimKiem = "";
    private String theLoaiHienTai = "Tất cả";

    // Khai báo giao diện (Interface) để gửi sự kiện ra ngoài
    public interface SuKienNhanBaiHat { 
        void khiNhanBaiHat(BaiHat baiHat); 
    }
    public interface SuKienNhanYeuThich { 
        void khiNhanYeuThich(BaiHat baiHat, boolean daYeuThich); 
    }

    // Hàm khởi tạo (Constructor)
    public BaiHatAdapter(Context context, List<BaiHat> danhSachBaiHat) {
        this.context = context;
        this.danhSachBaiHat = danhSachBaiHat;
        
        // Khởi tạo danh sách gốc để giữ lại toàn bộ bài hát khi tìm kiếm
        this.danhSachGoc = new ArrayList<>();
        for (BaiHat baiHat : danhSachBaiHat) {
            this.danhSachGoc.add(baiHat);
        }
        
        this.danhSachIdYeuThich = new ArrayList<>();
    }

    // Các hàm để gán sự kiện từ Activity vào Adapter
    public void datSuKienNhanBaiHat(SuKienNhanBaiHat suKien) { 
        this.suKienNhanBaiHat = suKien; 
    }
    public void datSuKienNhanYeuThich(SuKienNhanYeuThich suKien) { 
        this.suKienNhanYeuThich = suKien; 
    }

    // Cập nhật danh sách các bài hát yêu thích
    public void capNhatDanhSachYeuThich(List<Integer> danhSachId) {
        this.danhSachIdYeuThich = danhSachId;
        notifyDataSetChanged(); // Yêu cầu vẽ lại giao diện
    }

    // Cập nhật lại toàn bộ danh sách bài hát
    public void datDanhSachGoc(List<BaiHat> danhSachMoi) {
        // Xóa danh sách cũ và thêm dữ liệu mới vào
        this.danhSachGoc = new ArrayList<>();
        this.danhSachBaiHat = new ArrayList<>();
        
        for (BaiHat baiHat : danhSachMoi) {
            this.danhSachGoc.add(baiHat);
            this.danhSachBaiHat.add(baiHat);
        }
        notifyDataSetChanged();
    }

    // Hàm dùng để tìm kiếm và lọc danh sách theo thể loại
    public void locDanhSach(String tuKhoa, String theLoai) {
        // Chuẩn bị dữ liệu từ khóa
        if (tuKhoa != null) {
            this.tuKhoaTimKiem = tuKhoa.trim().toLowerCase();
        } else {
            this.tuKhoaTimKiem = "";
        }

        // Chuẩn bị dữ liệu thể loại
        if (theLoai != null) {
            this.theLoaiHienTai = theLoai;
        } else {
            this.theLoaiHienTai = "Tất cả";
        }

        // Xóa danh sách bài hát đang hiển thị để chuẩn bị thêm kết quả lọc
        danhSachBaiHat = new ArrayList<>();

        // Lặp qua toàn bộ danh sách gốc để kiểm tra từng bài hát
        for (BaiHat baiHat : danhSachGoc) {
            
            // 1. Kiểm tra Từ Khóa
            boolean phuHopTuKhoa = false;
            if (tuKhoaTimKiem.isEmpty()) {
                phuHopTuKhoa = true; // Nếu không nhập từ khóa thì luôn đúng
            } else {
                String tenBaiHat = baiHat.getTenBaiHat();
                String caSi = baiHat.getCaSi();
                
                if (tenBaiHat != null && tenBaiHat.toLowerCase().contains(tuKhoaTimKiem)) {
                    phuHopTuKhoa = true;
                } else if (caSi != null && caSi.toLowerCase().contains(tuKhoaTimKiem)) {
                    phuHopTuKhoa = true;
                }
            }

            // 2. Kiểm tra Thể Loại
            boolean phuHopTheLoai = false;
            if (theLoaiHienTai.equals("Tất cả")) {
                phuHopTheLoai = true; // Nếu chọn Tất cả thì luôn đúng
            } else {
                String theLoaiBaiHat = baiHat.getTheLoai();
                if (theLoaiBaiHat != null && theLoaiBaiHat.equalsIgnoreCase(theLoaiHienTai)) {
                    phuHopTheLoai = true;
                }
            }

            // Nếu bài hát phù hợp với cả 2 điều kiện thì thêm vào danh sách hiển thị
            if (phuHopTuKhoa && phuHopTheLoai) {
                danhSachBaiHat.add(baiHat);
            }
        }
        
        // Yêu cầu màn hình vẽ lại với danh sách mới
        notifyDataSetChanged();
    }

    // Khởi tạo một View mới (giao diện cho 1 item)
    @NonNull 
    @Override
    public HolderBaiHat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bai_hat, parent, false);
        return new HolderBaiHat(view);
    }

    // Đưa dữ liệu của bài hát vào trong giao diện
    @Override
    public void onBindViewHolder(@NonNull HolderBaiHat holder, int position) {
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

        // Gọi hàm phụ trợ để hiển thị ảnh bìa
        hienThiHinhAnh(baiHat.getHinhAnh(), holder.ivHinhAnh);

        // Kiểm tra bài hát này có đang nằm trong danh sách yêu thích không
        boolean daYeuThich = false;
        if (danhSachIdYeuThich.contains(baiHat.getId())) {
            daYeuThich = true;
        }

        // Thay đổi icon trái tim dựa vào trạng thái yêu thích
        if (daYeuThich) {
            holder.btnYeuThich.setImageResource(R.drawable.ic_favorite_filled);
            holder.btnYeuThich.setColorFilter(ContextCompat.getColor(context, R.color.primary_purple));
        } else {
            holder.btnYeuThich.setImageResource(R.drawable.ic_favorite_border);
            holder.btnYeuThich.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary));
        }

        // Bắt sự kiện khi người dùng nhấn vào toàn bộ bài hát
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienNhanBaiHat != null) {
                    suKienNhanBaiHat.khiNhanBaiHat(baiHat);
                }
            }
        });

        // Bắt sự kiện khi người dùng nhấn vào biểu tượng Trái Tim
        holder.btnYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienNhanYeuThich != null) {
                    boolean hienTaiDaYeuThich = danhSachIdYeuThich.contains(baiHat.getId());
                    suKienNhanYeuThich.khiNhanYeuThich(baiHat, hienTaiDaYeuThich);
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
    public static class HolderBaiHat extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenBaiHat, tvCaSi;
        ImageButton btnYeuThich;

        public HolderBaiHat(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các nút và chữ từ file XML
            ivHinhAnh = itemView.findViewById(R.id.ivHinhAnh);
            tvTenBaiHat = itemView.findViewById(R.id.tvTenBaiHat);
            tvCaSi = itemView.findViewById(R.id.tvCaSi);
            btnYeuThich = itemView.findViewById(R.id.btnYeuThich);
        }
    }
}
