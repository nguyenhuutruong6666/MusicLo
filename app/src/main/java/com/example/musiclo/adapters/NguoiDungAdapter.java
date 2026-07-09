package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclo.R;
import com.example.musiclo.models.NguoiDung;

import java.util.List;

public class NguoiDungAdapter extends RecyclerView.Adapter<NguoiDungAdapter.HolderNguoiDung> {

    // Các biến lưu trữ dữ liệu
    private Context context;
    private List<NguoiDung> danhSachNguoiDung;
    
    // Các biến xử lý sự kiện
    private SuKienDoiVaiTro suKienDoiVaiTro;
    private SuKienXoaNguoiDung suKienXoaNguoiDung;

    // Khai báo giao diện (Interface) để gửi sự kiện ra ngoài
    public interface SuKienDoiVaiTro { 
        void khiDoiVaiTro(NguoiDung nguoiDung, String vaiTroMoi); 
    }
    public interface SuKienXoaNguoiDung { 
        void khiXoaNguoiDung(NguoiDung nguoiDung); 
    }

    // Hàm khởi tạo (Constructor)
    public NguoiDungAdapter(Context context, List<NguoiDung> danhSachNguoiDung) {
        this.context = context;
        this.danhSachNguoiDung = danhSachNguoiDung;
    }

    // Các hàm để gán sự kiện từ Activity vào Adapter
    public void datSuKienDoiVaiTro(SuKienDoiVaiTro suKien) { 
        this.suKienDoiVaiTro = suKien; 
    }
    public void datSuKienXoaNguoiDung(SuKienXoaNguoiDung suKien) { 
        this.suKienXoaNguoiDung = suKien; 
    }

    // Khởi tạo một View mới (giao diện cho 1 item người dùng)
    @NonNull 
    @Override
    public HolderNguoiDung onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nguoi_dung, parent, false);
        return new HolderNguoiDung(view);
    }

    // Đưa dữ liệu của người dùng vào trong giao diện
    @Override
    public void onBindViewHolder(@NonNull HolderNguoiDung holder, int position) {
        NguoiDung nguoiDung = danhSachNguoiDung.get(position);

        // Hiển thị email (Nếu null thì để chuỗi rỗng)
        if (nguoiDung.getEmail() != null) {
            holder.tvEmail.setText(nguoiDung.getEmail());
        } else {
            holder.tvEmail.setText("");
        }

        // Hiển thị họ tên (Nếu null hoặc rỗng thì ghi "Chưa cập nhật")
        String hoTen = nguoiDung.getHoTen();
        if (hoTen != null && !hoTen.isEmpty()) {
            holder.tvHoTen.setText(hoTen);
        } else {
            holder.tvHoTen.setText("Chưa cập nhật");
        }
        
        // Lấy vai trò hiện tại
        String vaiTroHienTai = nguoiDung.getVaiTro();

        // Hiển thị vai trò và nút đổi vai trò tương ứng
        if ("admin".equals(vaiTroHienTai)) {
            holder.tvVaiTro.setText("Vai trò: Admin");
            holder.btnDoiVaiTro.setText("Đổi thành User");
        } else {
            holder.tvVaiTro.setText("Vai trò: Người dùng");
            holder.btnDoiVaiTro.setText("Đổi thành Admin");
        }

        // Bắt sự kiện khi nhấn nút Đổi Vai Trò
        holder.btnDoiVaiTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienDoiVaiTro != null) {
                    // Xác định vai trò mới để truyền ra ngoài
                    String vaiTroMoi;
                    if ("admin".equals(nguoiDung.getVaiTro())) {
                        vaiTroMoi = "user";
                    } else {
                        vaiTroMoi = "admin";
                    }
                    suKienDoiVaiTro.khiDoiVaiTro(nguoiDung, vaiTroMoi);
                }
            }
        });
        
        // Bắt sự kiện khi nhấn nút Xóa
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suKienXoaNguoiDung != null) {
                    suKienXoaNguoiDung.khiXoaNguoiDung(nguoiDung);
                }
            }
        });
    }

    // Đếm tổng số lượng người dùng
    @Override 
    public int getItemCount() { 
        if (danhSachNguoiDung != null) {
            return danhSachNguoiDung.size();
        } else {
            return 0;
        }
    }

    // Lớp chứa các thành phần giao diện của một Item Người Dùng
    public static class HolderNguoiDung extends RecyclerView.ViewHolder {
        TextView tvHoTen, tvEmail, tvVaiTro;
        Button btnDoiVaiTro, btnXoa;

        public HolderNguoiDung(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các nút và chữ từ file XML
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvVaiTro = itemView.findViewById(R.id.tvVaiTro);
            btnDoiVaiTro = itemView.findViewById(R.id.btnDoiVaiTro);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
