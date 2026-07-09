package com.example.musiclo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class QuanLyPhienDangNhap {

    private static final String TEN_PREF = "MusicLoPhien";
    private static final String KHOA_ID = "idNguoiDung";
    private static final String KHOA_EMAIL = "email";
    private static final String KHOA_VAI_TRO = "vaiTro";
    private static final String KHOA_HO_TEN = "hoTen";
    private static final String KHOA_DA_DANG_NHAP = "daDangNhap";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public QuanLyPhienDangNhap(Context context) {
        preferences = context.getSharedPreferences(TEN_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void luuPhien(int idNguoiDung, String email, String vaiTro, String hoTen) {
        editor.putInt(KHOA_ID, idNguoiDung);
        editor.putString(KHOA_EMAIL, email);
        editor.putString(KHOA_VAI_TRO, vaiTro);
        editor.putString(KHOA_HO_TEN, hoTen);
        editor.putBoolean(KHOA_DA_DANG_NHAP, true);
        editor.apply();
    }

    public int layIdNguoiDung() {
        return preferences.getInt(KHOA_ID, -1);
    }

    public String layEmail() {
        return preferences.getString(KHOA_EMAIL, "");
    }

    public String layVaiTro() {
        return preferences.getString(KHOA_VAI_TRO, "user");
    }

    public String layHoTen() {
        return preferences.getString(KHOA_HO_TEN, "");
    }

    public boolean daDangNhap() {
        return preferences.getBoolean(KHOA_DA_DANG_NHAP, false);
    }

    public void xoaPhien() {
        editor.clear();
        editor.apply();
    }

    public void capNhatVaiTro(String vaiTroMoi) {
        editor.putString(KHOA_VAI_TRO, vaiTroMoi);
        editor.apply();
    }

    public void capNhatHoTen(String hoTenMoi) {
        editor.putString(KHOA_HO_TEN, hoTenMoi);
        editor.apply();
    }
}
