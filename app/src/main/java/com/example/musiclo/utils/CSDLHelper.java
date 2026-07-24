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
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class CSDLHelper extends SQLiteOpenHelper {

    private static final String TEN_CSDL = "musiclo.db";
    private static final int PHIEN_BAN = 6;

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
    private Context mContext;

    private CSDLHelper(Context context) {
        super(context, TEN_CSDL, null, PHIEN_BAN);
        this.mContext = context;
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
                COT_ID_BH + " TEXT PRIMARY KEY, " +
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
                COT_ID_BH_YT + " TEXT NOT NULL, " +
                "UNIQUE(" + COT_ID_ND_YT + ", " + COT_ID_BH_YT + "))");

        // Thêm tài khoản admin mặc định
        ContentValues admin = new ContentValues();
        admin.put(COT_EMAIL, "admin@gmail.com");
        admin.put(COT_MAT_KHAU, "123456");
        admin.put(COT_VAI_TRO, "admin");
        admin.put(COT_HO_TEN, "Quản trị viên");
        db.insert(BANG_NGUOI_DUNG, null, admin);
        
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        java.util.Map<String, String> caSiMap = new java.util.HashMap<>();
        caSiMap.put("50 CUỘC GỌI NHỠ", "TINH HÀ \"SAY HI\"; CoolKid; Quang Hùng MasterD; Jaysonlei; CODYNAMVO");
        caSiMap.put("50 Năm Về Sau (Nghĩ Đến Ngày Chúng Ta Sẽ Già)", "Ducth Music & Thiện ND");
        caSiMap.put("Ai Ngoài Anh", "VSTRA, Tyronee");
        caSiMap.put("Âm Thầm Bên Em", "Sơn Tùng (M-TP)");
        caSiMap.put("Anh Đã Không Biết Cách Yêu Em", "Quang Đăng Trần");
        caSiMap.put("Anh Đã Lừa Dối Em Rồi (FUKA REMIX)", "Quang Kiệt");
        caSiMap.put("ANH VUI", "Phạm Kỳ");
        caSiMap.put("BAD NIGHT", "TINH HÀ \"SAY HI\"; Dương Domic; Pháp Kiều; DANG HONG HAI; Ali Hoàng Dương");
        caSiMap.put("Bình Yên", "Vũ. & Binz");
        caSiMap.put("Buông Đôi Tay Nhau Ra", "Sơn Tùng (M-TP)");
        caSiMap.put("chẳng phải tình đầu sao đau đến thế", "MIN, Dangrangto & antransax");
        caSiMap.put("Chất Gây Hại", "Quang Hùng MasterD, Low G, Hino");
        caSiMap.put("CHẠY NGAY ĐI", "Sơn Tùng M-TP");
        caSiMap.put("CHỜ ANH VỀ", "ANH TRAI \"SAY HI\", B Ray, AMEE");
        caSiMap.put("Chờ Tới Khi Anh Về", "HIEUTHUHAI, Hoàng Tôn");
        caSiMap.put("Chúng Ta Không Thuộc Về Nhau", "Sơn Tùng M-TP");
        caSiMap.put("Chuyện Đôi Ta", "Emcee L (Da LAB)");
        caSiMap.put("CÔ ĐƠN ANH CŨNG VUI", "TINH HÀ \"SAY HI\"; WEAN; KIMLONG; Xuân Định K.Y; DILLAN");
        caSiMap.put("DANCIN' MY WAY", "TINH HÀ \"SAY HI\"; buitruonglinh; Sơn.K; HYO; Song Luân");
        caSiMap.put("Dạo Bước HongKong 1999", "NHONHO");
        caSiMap.put("Dạo Gần Đây Anh Thấy Anh Không Bằng Ai Hết", "HIEUTHUHAI");
        caSiMap.put("Dạo Này", "Obito");
        caSiMap.put("Dạt Vào Tim Em", "Anh Trai Vượt Ngàn Chông Gai; CHARLES.; Thai VG; Cheng");
        caSiMap.put("đôi mắt kẻ tình si", "GREY D, MIN");
        caSiMap.put("Đớn Đau Vô Cùng", "DatKaa");
        caSiMap.put("dự báo thời tiết hôm nay mưa", "GREY D");
        caSiMap.put("Dù Cho Tận Thế", "Erik");
        caSiMap.put("Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP");
        caSiMap.put("E Là Không Thể", "Anh Quân Idol");
        caSiMap.put("Em (feat. SOOBIN)", "Binz");
        caSiMap.put("Em Thua Cô Ta (#1)", "THIÊN ĐÌNH");
        caSiMap.put("Giờ Thì", "buitruonglinh");
        caSiMap.put("HÀO QUANG", "RHYDER,Dương Domic,Pháp Kiều,ANH TRAI \"SAY HI\"");
        caSiMap.put("Hãy Trao Cho Anh", "Sơn Tùng M-TP, Snoop Dogg");
        caSiMap.put("hello em có khỏe không", "Dfoxie37, Myhoa, Tuann");
        caSiMap.put("Hẹn Lần Sau", "MAYDAYs");
        caSiMap.put("hoá ra…", "GREY D");
        caSiMap.put("Hỏa Tâm", "Anh Trai Vượt Ngàn Chông Gai feat. 34 Anh Tài");
        caSiMap.put("Hôn Lễ Của Em", "Trọng Nhân feat. Tiểu Mỹ");
        caSiMap.put("IDNAT (IM ĐỢI NGƯỜI ANH THƯƠNG)", "TINH HÀ \"SAY HI\"; Wren Evans; IVAN; CAPTAIN BOY; Thể Thiên");
        caSiMap.put("Kẻ Say Tình 2", "Quốc Thiên");
        caSiMap.put("Kẻ Say Tình", "Quốc Thiên");
        caSiMap.put("Kho Báu", "(S)TRONG");
        caSiMap.put("Kho Báu (1)", "(S)TRONG, Rhymastic");
        caSiMap.put("Khó Vẽ Nụ Cười", "Dat G,Du Uyen");
        caSiMap.put("Khóa Ly Biệt", "The Masked Singer");
        caSiMap.put("Không Buông", "Hngle,Ari (Việt Nam)");
        caSiMap.put("KHÔNG ĐAU NỮA RỒI", "EM XINH \"SAY HI\", 52Hz, Orange, Châu Bùi, Mỹ Mỹ, Pháp Kiều");
        caSiMap.put("Không Thể Say", "HIEUTHUHAI");
        caSiMap.put("Không Thời Gian", "Dương Domic");

        String[] theLoaiList = {"Pop", "Rock", "Ballad", "V-Pop", "R&B", "Rap", "Khác"};
        String[] moTaList = {
                "Bài hát tuyệt vời cho ngày mới",
                "Giai điệu nhẹ nhàng sâu lắng",
                "Nhạc quẩy cực sung",
                "Nghe đi nghe lại không chán",
                "Cảm xúc thăng hoa",
                "Một bản hit đình đám",
                "Giai điệu bắt tai"
        };

        try {
            String[] fileNames = mContext.getAssets().list("datamusic");
            if (fileNames != null) {
                File thuMuc = new File(mContext.getFilesDir(), "music");
                if (!thuMuc.exists()) {
                    thuMuc.mkdirs();
                }

                Random random = new Random();
                for (String fileName : fileNames) {
                    if (fileName.endsWith(".mp3")) {
                        File fileDich = new File(thuMuc, fileName);
                        if (!fileDich.exists()) {
                            try (InputStream luongDoc = mContext.getAssets().open("datamusic/" + fileName);
                                 FileOutputStream luongGhi = new FileOutputStream(fileDich)) {
                                byte[] boDem = new byte[16384];
                                int soByteDa;
                                while ((soByteDa = luongDoc.read(boDem)) != -1) {
                                    luongGhi.write(boDem, 0, soByteDa);
                                }
                            }
                        }

                        String tenBaiHat = fileName.substring(0, fileName.lastIndexOf('.'));
                        String caSi = caSiMap.containsKey(tenBaiHat) ? caSiMap.get(tenBaiHat) : "Đang cập nhật";
                        String theLoai = theLoaiList[random.nextInt(theLoaiList.length)];
                        String moTa = moTaList[random.nextInt(moTaList.length)];
                        String hinhAnh = ""; // Mặc định
                        String linkBaiHat = fileDich.getAbsolutePath();

                        String idBaiHat = java.util.UUID.randomUUID().toString();

                        ContentValues giaTriMoi = new ContentValues();
                        giaTriMoi.put(COT_ID_BH, idBaiHat);
                        giaTriMoi.put(COT_TEN_BAI_HAT, tenBaiHat);
                        giaTriMoi.put(COT_CA_SI, caSi);
                        giaTriMoi.put(COT_THE_LOAI, theLoai);
                        giaTriMoi.put(COT_MO_TA, moTa);
                        giaTriMoi.put(COT_HINH_ANH, hinhAnh);
                        giaTriMoi.put(COT_LINK_BAI_HAT, linkBaiHat);
                        db.insert(BANG_BAI_HAT, null, giaTriMoi);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int phienBanCu, int phienBanMoi) {
        db.execSQL("DROP TABLE IF EXISTS " + BANG_YEU_THICH);
        db.execSQL("DROP TABLE IF EXISTS " + BANG_BAI_HAT);
        db.execSQL("DROP TABLE IF EXISTS " + BANG_NGUOI_DUNG);
        onCreate(db);
    }

    // NGƯỜI DÙNG
    // Thêm người dùng mới. Trả về id nếu thành công, -1 nếu email đã tồn tại.
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

    // BÀI HÁT
    
    //Đảm bảo bài hát tồn tại trong DB. Dùng khi yêu thích bài từ Audius.
    //Nếu id đã có → giữ nguyên (INSERT OR IGNORE). Nếu chưa có → thêm mới.
    public void damBaoBaiHatTonTai(com.example.musiclo.models.BaiHat baiHat) {
        if (baiHat == null || baiHat.getId() == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_ID_BH, baiHat.getId());
        giaTriMoi.put(COT_TEN_BAI_HAT, baiHat.getTenBaiHat() != null ? baiHat.getTenBaiHat() : "");
        giaTriMoi.put(COT_CA_SI, baiHat.getCaSi() != null ? baiHat.getCaSi() : "");
        giaTriMoi.put(COT_THE_LOAI, baiHat.getTheLoai() != null ? baiHat.getTheLoai() : "Khác");
        giaTriMoi.put(COT_MO_TA, baiHat.getMoTa() != null ? baiHat.getMoTa() : "");
        giaTriMoi.put(COT_HINH_ANH, baiHat.getHinhAnh() != null ? baiHat.getHinhAnh() : "");
        giaTriMoi.put(COT_LINK_BAI_HAT, baiHat.getLinkBaiHat() != null ? baiHat.getLinkBaiHat() : "");
        // INSERT OR IGNORE: không ghi đè nếu đã tồn tại
        db.insertWithOnConflict(BANG_BAI_HAT, null, giaTriMoi, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public String themBaiHat(String tenBaiHat, String caSi, String theLoai,
                           String moTa, String hinhAnh, String linkBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        String idBaiHat = java.util.UUID.randomUUID().toString();
        giaTriMoi.put(COT_ID_BH, idBaiHat);
        giaTriMoi.put(COT_TEN_BAI_HAT, tenBaiHat);
        giaTriMoi.put(COT_CA_SI, caSi);
        giaTriMoi.put(COT_THE_LOAI, theLoai);
        giaTriMoi.put(COT_MO_TA, moTa);
        giaTriMoi.put(COT_HINH_ANH, hinhAnh);
        giaTriMoi.put(COT_LINK_BAI_HAT, linkBaiHat);
        long ketQua = db.insert(BANG_BAI_HAT, null, giaTriMoi);
        db.close();
        return ketQua != -1 ? idBaiHat : null;
    }

    public boolean capNhatBaiHat(String idBaiHat, String tenBaiHat, String caSi, String theLoai,
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
                COT_ID_BH + "=?", new String[]{idBaiHat});
        db.close();
        return soHang > 0;
    }

    public boolean xoaBaiHat(String idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(BANG_YEU_THICH, COT_ID_BH_YT + "=?", new String[]{idBaiHat});
        int soHang = db.delete(BANG_BAI_HAT, COT_ID_BH + "=?", new String[]{idBaiHat});
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

    public BaiHat layBaiHatTheoId(String idBaiHat) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_BAI_HAT, null,
                COT_ID_BH + "=?", new String[]{idBaiHat},
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
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COT_ID_BH));
        String tenBaiHat = cursor.getString(cursor.getColumnIndexOrThrow(COT_TEN_BAI_HAT));
        String caSi = cursor.getString(cursor.getColumnIndexOrThrow(COT_CA_SI));
        String theLoai = cursor.getString(cursor.getColumnIndexOrThrow(COT_THE_LOAI));
        String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COT_MO_TA));
        String hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow(COT_HINH_ANH));
        String linkBaiHat = cursor.getString(cursor.getColumnIndexOrThrow(COT_LINK_BAI_HAT));
        return new BaiHat(id, tenBaiHat, caSi, theLoai, moTa, hinhAnh, linkBaiHat);
    }

    //YÊU THÍCH
    public boolean themYeuThich(int idNguoiDung, String idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues giaTriMoi = new ContentValues();
        giaTriMoi.put(COT_ID_ND_YT, idNguoiDung);
        giaTriMoi.put(COT_ID_BH_YT, idBaiHat);
        long ketQua = db.insertOrThrow(BANG_YEU_THICH, null, giaTriMoi);
        db.close();
        return ketQua != -1;
    }

    public boolean xoaYeuThich(int idNguoiDung, String idBaiHat) {
        SQLiteDatabase db = getWritableDatabase();
        int soHang = db.delete(BANG_YEU_THICH,
                COT_ID_ND_YT + "=? AND " + COT_ID_BH_YT + "=?",
                new String[]{String.valueOf(idNguoiDung), idBaiHat});
        db.close();
        return soHang > 0;
    }

    public boolean laYeuThich(int idNguoiDung, String idBaiHat) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_YEU_THICH, new String[]{COT_ID_YT},
                COT_ID_ND_YT + "=? AND " + COT_ID_BH_YT + "=?",
                new String[]{String.valueOf(idNguoiDung), idBaiHat},
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

    public List<String> layIdYeuThich(int idNguoiDung) {
        List<String> danhSachId = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BANG_YEU_THICH, new String[]{COT_ID_BH_YT},
                COT_ID_ND_YT + "=?", new String[]{String.valueOf(idNguoiDung)},
                null, null, null);
        while (cursor.moveToNext()) {
            danhSachId.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return danhSachId;
    }
}
