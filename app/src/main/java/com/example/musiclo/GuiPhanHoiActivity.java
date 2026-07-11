package com.example.musiclo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tieuDe = edtTieuDe.getText().toString().trim();
                String noiDung = edtNoiDung.getText().toString().trim();

                if (tieuDe.isEmpty()) {
                    Toast.makeText(GuiPhanHoiActivity.this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (noiDung.isEmpty()) {
                    Toast.makeText(GuiPhanHoiActivity.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String fromEmail = "nguyenhuutruongchatgpt@gmail.com";
                    String passWord = "xuysbactoasarzzr"; // App Password không có dấu cách
                    String toEmail = "texclostore@gmail.com";
                    String host = "smtp.gmail.com";

                    // Lấy thông tin người gửi từ phiên đăng nhập
                    String hoTen = quanLyPhienDangNhap.layHoTen();
                    String emailND = quanLyPhienDangNhap.layEmail();
                    if (hoTen == null || hoTen.isEmpty())
                        hoTen = "Người dùng MusicLo";

                    String subject = "[MusicLo Feedback] " + tieuDe;
                    String content = "Người gửi: " + hoTen + " (" + emailND + ")\n\n"
                            + "Nội dung phản hồi:\n" + noiDung;

                    // Dùng new Properties() thay vì System.getProperties() để tránh lẫn cấu hình cũ
                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", host);
                    properties.put("mail.smtp.port", "465");
                    properties.put("mail.smtp.ssl.enable", "true"); // phải là String, không phải boolean
                    properties.put("mail.smtp.auth", "true"); // phải là String, không phải boolean

                    Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail, passWord);
                        }
                    });

                    MimeMessage mimeMessage = new MimeMessage(session);
                    mimeMessage.addRecipients(Message.RecipientType.TO,
                            String.valueOf(new InternetAddress(toEmail)));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setText(content);

                    Thread emailThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Transport.send(mimeMessage);
                                // Gửi thành công → cập nhật UI trên main thread
                                runOnUiThread(() -> {
                                    edtTieuDe.setText("");
                                    edtNoiDung.setText("");
                                    Toast.makeText(GuiPhanHoiActivity.this,
                                            "Phản hồi đã được gửi, cảm ơn bạn ❤️", Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception e) {
                                Log.d("Lỗi thread email", e.toString());
                                // Gửi thất bại → thông báo lỗi trên main thread
                                runOnUiThread(() -> Toast.makeText(GuiPhanHoiActivity.this,
                                        "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            }
                        }
                    });
                    emailThread.start();

                } catch (Exception e) {
                    Log.d("Lỗi gửi email", e.toString());
                    Toast.makeText(GuiPhanHoiActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
