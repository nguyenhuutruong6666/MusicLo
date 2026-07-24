package com.example.musiclo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musiclo.adapters.BaiHatAdapter;
import com.example.musiclo.models.BaiHat;
import com.example.musiclo.utils.CSDLHelper;
import com.example.musiclo.utils.QuanLyPhienDangNhap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import android.widget.ProgressBar;

import com.example.musiclo.api.AudiusApiClient;
import com.example.musiclo.api.AudiusApiService;
import com.example.musiclo.api.AudiusResponse;
import com.example.musiclo.api.AudiusTrack;
import com.example.musiclo.api.AudiusTrackResponse;
import com.example.musiclo.utils.MusicPlayerManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachBaiHatActivity extends AppCompatActivity implements View.OnClickListener {

    ListView rvDanhSachBaiHat;
    EditText edtTimKiem;
    Spinner spinnerTheLoai;
    TextView tvTrong;
    BottomNavigationView bottomNav;
    ProgressBar progressBar;

    BaiHatAdapter baiHatAdapter;
    ArrayList<BaiHat> danhSachGoc;
    ArrayList<BaiHat> danhSachHienThi;
    ArrayList<String> danhSachIdYeuThich;

    CSDLHelper csdlHelper;
    QuanLyPhienDangNhap quanLyPhienDangNhap;

    String tuKhoaTimKiem = "";
    String theLoaiHienTai = "Tất cả";
    String[] DANH_SACH_THE_LOAI = {"Tất cả", "Pop", "Rock", "Ballad", "V-Pop", "R&B", "Rap", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_bai_hat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csdlHelper = CSDLHelper.layThucThe(this);
        quanLyPhienDangNhap = new QuanLyPhienDangNhap(this);

        rvDanhSachBaiHat = findViewById(R.id.rvDanhSachBaiHat);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        spinnerTheLoai = findViewById(R.id.spinnerTheLoai);
        tvTrong = findViewById(R.id.tvTrong);
        bottomNav = findViewById(R.id.thanhDieuHuongDuoi);
        progressBar = findViewById(R.id.progressBar);

        danhSachGoc = new ArrayList<>();
        danhSachHienThi = new ArrayList<>();
        danhSachIdYeuThich = new ArrayList<>();
        
        baiHatAdapter = new BaiHatAdapter(this, R.layout.item_bai_hat, danhSachHienThi, danhSachIdYeuThich);
        rvDanhSachBaiHat.setAdapter(baiHatAdapter);

        rvDanhSachBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayerManager.getInstance().setPlaylist(danhSachHienThi);
                
                Intent intent = new Intent(DanhSachBaiHatActivity.this, PhatNhacActivity.class);
                intent.putExtra("viTriHienTai", position);
                startActivity(intent);
            }
        });
        
        ArrayAdapter<String> theLoaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DANH_SACH_THE_LOAI);
        theLoaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheLoai.setAdapter(theLoaiAdapter);

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tuKhoaTimKiem = s.toString();
                locDanhSach();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerTheLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theLoaiHienTai = DANH_SACH_THE_LOAI[position];
                locDanhSach();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, DanhSachYeuThichActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, TrangCaNhanActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachGoc();
        taiDanhSachYeuThich();
    }

    private void taiDanhSachGoc() {
        danhSachGoc.clear();
        // Lấy nhạc cục bộ
        danhSachGoc.addAll(csdlHelper.layTatCaBaiHat());
        locDanhSach();
        
        // Lấy nhạc từ Audius API
        taiNhacTuAudius();
    }

    private void taiNhacTuAudius() {
        progressBar.setVisibility(View.VISIBLE);
        AudiusApiService initialService = AudiusApiClient.getClient("https://api.audius.co/");
        initialService.getHosts("https://api.audius.co/").enqueue(new Callback<AudiusResponse>() {
            @Override
            public void onResponse(Call<AudiusResponse> call, Response<AudiusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getHosts() != null && !response.body().getHosts().isEmpty()) {
                    String host = response.body().getHosts().get(0);
                    AudiusApiService audiusService = AudiusApiClient.getClient(host);
                    audiusService.getTrendingTracks("MusicLo").enqueue(new Callback<AudiusTrackResponse>() {
                        @Override
                        public void onResponse(Call<AudiusTrackResponse> call, Response<AudiusTrackResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                for (AudiusTrack track : response.body().getData()) {
                                    String id = track.getId();
                                    String ten = track.getTitle();
                                    String caSi = track.getUser() != null ? track.getUser().getName() : "Unknown";
                                    String theLoai = track.getGenre() != null ? track.getGenre() : "Khác";
                                    String moTa = track.getDescription();
                                    String hinhAnh = (track.getArtwork() != null && track.getArtwork().getImage480() != null) ? track.getArtwork().getImage480() : "";
                                    String linkNhac = host + "/v1/tracks/" + id + "/stream?app_name=MusicLo";
                                    
                                    BaiHat bh = new BaiHat(id, ten, caSi, theLoai, moTa, hinhAnh, linkNhac);
                                    danhSachGoc.add(bh);
                                }
                                locDanhSach();
                            } else {
                                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi API getTrending: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<AudiusTrackResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi tải nhạc: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi API getHosts: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AudiusResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi getHosts: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void taiDanhSachYeuThich() {
        int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
        if (idNguoiDung != -1) {
            danhSachIdYeuThich.clear();
            danhSachIdYeuThich.addAll(csdlHelper.layIdYeuThich(idNguoiDung));
            baiHatAdapter.notifyDataSetChanged();
        }
    }

    private void locDanhSach() {
        danhSachHienThi.clear();
        
        String tuKhoa = tuKhoaTimKiem != null ? tuKhoaTimKiem.trim().toLowerCase() : "";
        
        for (BaiHat baiHat : danhSachGoc) {
            boolean phuHopTuKhoa = false;
            if (tuKhoa.isEmpty()) {
                phuHopTuKhoa = true;
            } else {
                String tenBaiHat = baiHat.getTenBaiHat();
                String caSi = baiHat.getCaSi();
                if (tenBaiHat != null && tenBaiHat.toLowerCase().contains(tuKhoa)) {
                    phuHopTuKhoa = true;
                } else if (caSi != null && caSi.toLowerCase().contains(tuKhoa)) {
                    phuHopTuKhoa = true;
                }
            }

            boolean phuHopTheLoai = false;
            if (theLoaiHienTai.equals("Tất cả")) {
                phuHopTheLoai = true;
            } else {
                String theLoaiBaiHat = baiHat.getTheLoai();
                if (theLoaiBaiHat != null && theLoaiBaiHat.equalsIgnoreCase(theLoaiHienTai)) {
                    phuHopTheLoai = true;
                }
            }

            if (phuHopTuKhoa && phuHopTheLoai) {
                danhSachHienThi.add(baiHat);
            }
        }
        
        baiHatAdapter.notifyDataSetChanged();
        
        if (danhSachHienThi.isEmpty()) {
            tvTrong.setVisibility(View.VISIBLE);
            rvDanhSachBaiHat.setVisibility(View.GONE);
        } else {
            tvTrong.setVisibility(View.GONE);
            rvDanhSachBaiHat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnYeuThich) {
            BaiHat baiHat = (BaiHat) v.getTag();
            int idNguoiDung = quanLyPhienDangNhap.layIdNguoiDung();
            if (idNguoiDung == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean hienTaiDaYeuThich = danhSachIdYeuThich.contains(baiHat.getId());
            if (hienTaiDaYeuThich) {
                csdlHelper.xoaYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                csdlHelper.themYeuThich(idNguoiDung, baiHat.getId());
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
            taiDanhSachYeuThich();
        }
    }
}
