package com.example.musiclo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.utils.QuanLyPhienDangNhap;

public class GuiPhanHoiActivity extends AppCompatActivity {

    ImageButton btnQuayLai;
    EditText edtTieuDe, edtNoiDung;
    Button btnGui;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gui_phan_hoi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        btnQuayLai = findViewById(R.id.btnQuayLai);
        edtTieuDe = findViewById(R.id.edtTieuDe);
        edtNoiDung = findViewById(R.id.edtNoiDung);
        btnGui = findViewById(R.id.btnGui);

        btnQuayLai.setOnClickListener(v -> finish());
        btnGui.setOnClickListener(v -> xuLyGuiPhanHoi());
    }

    private void xuLyGuiPhanHoi() {
        String tieuDe = edtTieuDe.getText().toString().trim();
        String noiDung = edtNoiDung.getText().toString().trim();

        if (tieuDe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noiDung.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailNguoiDung = quanLyPhienDangNhap.layEmail();
        String hoTen = quanLyPhienDangNhap.layHoTen();
        if (hoTen == null || hoTen.isEmpty()) {
            hoTen = "Người dùng MusicLo";
        }

        String emailBody = "Người gửi: " + hoTen + " (" + emailNguoiDung + ")\n\n" +
                "Nội dung phản hồi:\n" + noiDung;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:nguyenhuutruongchatgpt@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "[MusicLo Feedback] " + tieuDe);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        try {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng Email để gửi..."));
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng Email nào trên máy!", Toast.LENGTH_SHORT).show();
        }
    }
}
