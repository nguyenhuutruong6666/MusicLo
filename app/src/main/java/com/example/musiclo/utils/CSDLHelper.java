package com.example.musiclo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.musiclo.models.BaiHat;
import com.example.musiclo.models.NguoiDung;

import java.util.ArrayList;
import java.util.List;

public class CSDLHelper extends SQLiteOpenHelper {

    private static final String TEN_CSDL = "musiclo.db";
    private static final int PHIEN_BAN = 3;

    // Tên bảng
    private static final String BANG_NGUOI_DUNG = "users";
    private static final String BANG_BAI_HAT = "songs";
    private static final String BANG_YEU_THICH = "favorites";

    // Cột bảng users
    private static final String COT_ID_ND = "id";
    private static final String COT_EMAIL = "email";
    private static final String COT_MAT_KHAU = "password";
    private static final String COT_VAI_TRO = "role";
    private static final String COT_HO_TEN = "fullName";

    // Cột bảng songs
    private static final String COT_ID_BH = "id";
    private static final String COT_TEN_BAI_HAT = "title";
    private static final String COT_CA_SI = "artist";
    private static final String COT_THE_LOAI = "category";
    private static final String COT_MO_TA = "description";
    private static final String COT_HINH_ANH = "imagePath";
    private static final String COT_LINK_BAI_HAT = "mp3Path";

    // Cột bảng favorites
    private static final String COT_ID_YT = "id";
    private static final String COT_ID_ND_YT = "userId";
    private static final String COT_ID_BH_YT = "songId";

    private static CSDLHelper thucThe;

    private CSDLHelper(Context context) {
        super(context, TEN_CSDL, null, PHIEN_BAN);
    }

    public static synchronized CSDLHelper layThucThe(Context context) {
        if (thucThe == null) {
            thucThe = new CSDLHelper(context.getApplicationContext());
        }
        return thucThe;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng người dùng
        db.execSQL("CREATE TABLE " + BANG_NGUOI_DUNG + " (" +
                COT_ID_ND + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COT_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COT_MAT_KHAU + " TEXT NOT NULL, " +
                COT_VAI_TRO + " TEXT NOT NULL DEFAULT 'user', " +
                COT_HO_TEN + " TEXT)");

        // Tạo bảng bài hát
        db.execSQL("CREATE TABLE " + BANG_BAI_HAT + " (" +
                COT_ID_BH + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COT_TEN_BAI_HAT + " TEXT NOT NULL, " +
                COT_CA_SI + " TEXT, " +
                COT_THE_LOAI + " TEXT, " +
                COT_MO_TA + " TEXT, " +
                COT_HINH_ANH + " TEXT, " +
                COT_LINK_BAI_HAT + " TEXT)");

        // Tạo bảng yêu thích
        db.execSQL("CREATE TABLE " + BANG_YEU_THICH + " (" +
                COT_ID_YT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COT_ID_ND_YT + " INTEGER NOT NULL, " +
                COT_ID_BH_YT + " INTEGER NOT NULL, " +
                "UNIQUE(" + COT_ID_ND_YT + ", " + COT_ID_BH_YT + "))");

        // Thêm tài khoản admin mặc định
        ContentValues admin = new ContentValues();
        admin.put(COT_EMAIL, "admin@gmail.com");
        admin.put(COT_MAT_KHAU, "123456");
        admin.put(COT_VAI_TRO, "admin");
        admin.put(COT_HO_TEN, "Quản trị viên");
        db.insert(BANG_NGUOI_DUNG, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int phienBanCu, int phienBanMoi) {
        db.execSQL("DROP TABLE IF EXISTS " + BANG_YEU_THICH);
        db.execSQL("DROP TABLE IF EXISTS " + BANG_BAI_HAT);
        db.execSQL("DROP TABLE IF EXISTS " + BANG_NGUOI_DUNG);
        onCreate(db);
    }

    // ==================== NGƯỜI DÙNG ====================

    /** Thêm người dùng mới. Trả về id nếu thành công, -1 nếu email đã tồn tại. */
    public long themNguoiDung(String email, String matKhau, String hoTen) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_EMAIL, email);
        giaTriMoi.put(COT_MAT_KHAU, matKhau);
        giaTriMoi.put(COT_VAI_TRO, "user");
        giaTriMoi.put(COT_HO_TEN, hoTen);
        long ketQua = db.insert(BANG_NGUOI_DUNG, null, giaTriMoi);
        db.close();
        return ketQua;
    }

    /** Kiểm tra đăng nhập bằng email + mật khẩu. */
    public NguoiDung kiemTraDangNhap(String email, String matKhau) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_NGUOI_DUNG, null,
                COT_EMAIL + "=? AND " + COT_MAT_KHAU + "=?",
                new String[]{email, matKhau}, null, null, null);
        NguoiDung nguoiDung = null;
        if (cursor.moveToFirst()) {
            nguoiDung = cursorSangNguoiDung(cursor);
        }
        cursor.close();
        db.close();
        return nguoiDung;
    }

    public NguoiDung layNguoiDungTheoId(int idNguoiDung) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_NGUOI_DUNG, null,
                COT_ID_ND + "=?", new String[]{String.valueOf(idNguoiDung)},
                null, null, null);
        NguoiDung nguoiDung = null;
        if (cursor.moveToFirst()) {
            nguoiDung = cursorSangNguoiDung(cursor);
        }
        cursor.close();
        db.close();
        return nguoiDung;
    }

    public List<NguoiDung> layTatCaNguoiDung() {
        List<NguoiDung> danhSach = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_NGUOI_DUNG, null, null, null, null, null, COT_ID_ND + " ASC");
        while (cursor.moveToNext()) {
            danhSach.add(cursorSangNguoiDung(cursor));
        }
        cursor.close();
        db.close();
        return danhSach;
    }

    public boolean capNhatVaiTro(int idNguoiDung, String vaiTroMoi) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_VAI_TRO, vaiTroMoi);
        int soHang = db.update(BANG_NGUOI_DUNG, giaTriMoi,
                COT_ID_ND + "=?", new String[]{String.valueOf(idNguoiDung)});
        db.close();
        return soHang > 0;
    }

    public boolean xoaNguoiDung(int idNguoiDung) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(BANG_YEU_THICH, COT_ID_ND_YT + "=?", new String[]{String.valueOf(idNguoiDung)});
        int soHang = db.delete(BANG_NGUOI_DUNG, COT_ID_ND + "=?", new String[]{String.valueOf(idNguoiDung)});
        db.close();
        return soHang > 0;
    }

    private NguoiDung cursorSangNguoiDung(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COT_ID_ND));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COT_EMAIL));
        String matKhau = cursor.getString(cursor.getColumnIndexOrThrow(COT_MAT_KHAU));
        String vaiTro = cursor.getString(cursor.getColumnIndexOrThrow(COT_VAI_TRO));
        String hoTen = cursor.getString(cursor.getColumnIndexOrThrow(COT_HO_TEN));
        return new NguoiDung(id, email, matKhau, vaiTro, hoTen);
    }

    // ==================== BÀI HÁT ====================

    public long themBaiHat(String tenBaiHat, String caSi, String theLoai,
                           String moTa, String hinhAnh, String linkBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_TEN_BAI_HAT, tenBaiHat);
        giaTriMoi.put(COT_CA_SI, caSi);
        giaTriMoi.put(COT_THE_LOAI, theLoai);
        giaTriMoi.put(COT_MO_TA, moTa);
        giaTriMoi.put(COT_HINH_ANH, hinhAnh);
        giaTriMoi.put(COT_LINK_BAI_HAT, linkBaiHat);
        long ketQua = db.insert(BANG_BAI_HAT, null, giaTriMoi);
        db.close();
        return ketQua;
    }

    public boolean capNhatBaiHat(int idBaiHat, String tenBaiHat, String caSi, String theLoai,
                                  String moTa, String hinhAnh, String linkBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_TEN_BAI_HAT, tenBaiHat);
        giaTriMoi.put(COT_CA_SI, caSi);
        giaTriMoi.put(COT_THE_LOAI, theLoai);
        giaTriMoi.put(COT_MO_TA, moTa);
        giaTriMoi.put(COT_HINH_ANH, hinhAnh);
        giaTriMoi.put(COT_LINK_BAI_HAT, linkBaiHat);
        int soHang = db.update(BANG_BAI_HAT, giaTriMoi,
                COT_ID_BH + "=?", new String[]{String.valueOf(idBaiHat)});
        db.close();
        return soHang > 0;
    }

    public boolean xoaBaiHat(int idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(BANG_YEU_THICH, COT_ID_BH_YT + "=?", new String[]{String.valueOf(idBaiHat)});
        int soHang = db.delete(BANG_BAI_HAT, COT_ID_BH + "=?", new String[]{String.valueOf(idBaiHat)});
        db.close();
        return soHang > 0;
    }

    public List<BaiHat> layTatCaBaiHat() {
        List<BaiHat> danhSach = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_BAI_HAT, null, null, null, null, null, COT_ID_BH + " DESC");
        while (cursor.moveToNext()) {
            danhSach.add(cursorSangBaiHat(cursor));
        }
        cursor.close();
        db.close();
        return danhSach;
    }

    public BaiHat layBaiHatTheoId(int idBaiHat) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_BAI_HAT, null,
                COT_ID_BH + "=?", new String[]{String.valueOf(idBaiHat)},
                null, null, null);
        BaiHat baiHat = null;
        if (cursor.moveToFirst()) {
            baiHat = cursorSangBaiHat(cursor);
        }
        cursor.close();
        db.close();
        return baiHat;
    }

    private BaiHat cursorSangBaiHat(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COT_ID_BH));
        String tenBaiHat = cursor.getString(cursor.getColumnIndexOrThrow(COT_TEN_BAI_HAT));
        String caSi = cursor.getString(cursor.getColumnIndexOrThrow(COT_CA_SI));
        String theLoai = cursor.getString(cursor.getColumnIndexOrThrow(COT_THE_LOAI));
        String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COT_MO_TA));
        String hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow(COT_HINH_ANH));
        String linkBaiHat = cursor.getString(cursor.getColumnIndexOrThrow(COT_LINK_BAI_HAT));
        return new BaiHat(id, tenBaiHat, caSi, theLoai, moTa, hinhAnh, linkBaiHat);
    }

    // ==================== YÊU THÍCH ====================

    public boolean themYeuThich(int idNguoiDung, int idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_ID_ND_YT, idNguoiDung);
        giaTriMoi.put(COT_ID_BH_YT, idBaiHat);
        long ketQua = db.insertOrThrow(BANG_YEU_THICH, null, giaTriMoi);
        db.close();
        return ketQua != -1;
    }

    public boolean xoaYeuThich(int idNguoiDung, int idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        int soHang = db.delete(BANG_YEU_THICH,
                COT_ID_ND_YT + "=? AND " + COT_ID_BH_YT + "=?",
                new String[]{String.valueOf(idNguoiDung), String.valueOf(idBaiHat)});
        db.close();
        return soHang > 0;
    }

    public boolean laYeuThich(int idNguoiDung, int idBaiHat) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_YEU_THICH, new String[]{COT_ID_YT},
                COT_ID_ND_YT + "=? AND " + COT_ID_BH_YT + "=?",
                new String[]{String.valueOf(idNguoiDung), String.valueOf(idBaiHat)},
                null, null, null);
        boolean tonTai = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return tonTai;
    }

    public List<BaiHat> layDanhSachYeuThich(int idNguoiDung) {
        List<BaiHat> danhSach = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT s.* FROM " + BANG_BAI_HAT + " s " +
                "INNER JOIN " + BANG_YEU_THICH + " f ON s." + COT_ID_BH + " = f." + COT_ID_BH_YT +
                " WHERE f." + COT_ID_ND_YT + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idNguoiDung)});
        while (cursor.moveToNext()) {
            danhSach.add(cursorSangBaiHat(cursor));
        }
        cursor.close();
        db.close();
        return danhSach;
    }

    public List<Integer> layIdYeuThich(int idNguoiDung) {
        List<Integer> danhSachId = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_YEU_THICH, new String[]{COT_ID_BH_YT},
                COT_ID_ND_YT + "=?", new String[]{String.valueOf(idNguoiDung)},
                null, null, null);
        while (cursor.moveToNext()) {
            danhSachId.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return danhSachId;
    }
}
