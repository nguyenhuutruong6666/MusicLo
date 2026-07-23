package com.example.musiclo.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class LuuTruCucBo {

    private static final String THU_MUC_ANH = "images";
    private static final String THU_MUC_NHAC = "music";

    // Lưu ảnh từ Uri về thư mục nội bộ của app. Trả về đường dẫn hoặc null nếu lỗi
    public static String luuAnhVeMay(Context context, Uri uriAnh) {
        return luuFileVeMay(context, uriAnh, THU_MUC_ANH, ".jpg");
    }

    // Lưu file MP3 từ Uri về thư mục nội bộ của app. Trả về đường dẫn hoặc null nếu lỗi
    public static String luuNhacVeMay(Context context, Uri uriNhac) {
        return luuFileVeMay(context, uriNhac, THU_MUC_NHAC, ".mp3");
    }

    private static String luuFileVeMay(Context context, Uri uriFile,
                                        String thuMucCon, String duoiFile) {
        File thuMuc = new File(context.getFilesDir(), thuMucCon);
        if (!thuMuc.exists()) {
            thuMuc.mkdirs();
        }
        String tenFile = UUID.randomUUID().toString() + duoiFile;
        File fileDich = new File(thuMuc, tenFile);

        try (InputStream luongDoc = context.getContentResolver().openInputStream(uriFile);
             OutputStream luongGhi = new FileOutputStream(fileDich)) {
            if (luongDoc == null) return null;
            byte[] boDem = new byte[16384];
            int soByteDa;
            while ((soByteDa = luongDoc.read(boDem)) != -1) {
                luongGhi.write(boDem, 0, soByteDa);
            }
            return fileDich.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    // Xóa file tại đường dẫn chỉ định
    public static void xoaFile(String duongDan) {
        if (duongDan != null && !duongDan.isEmpty()) {
            File file = new File(duongDan);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
