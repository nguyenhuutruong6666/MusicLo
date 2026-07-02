package com.example.musicapp.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CloudinaryUploader - Upload file lên Cloudinary sử dụng Unsigned Upload Preset.
 *
 * LƯU Ý: Bạn cần tạo một Unsigned Upload Preset trên Cloudinary:
 * 1. Đăng nhập vào https://cloudinary.com/
 * 2. Vào Settings > Upload > Upload presets
 * 3. Bấm "Add upload preset"
 * 4. Đặt tên là "music_unsigned_preset"
 * 5. Signing mode: Unsigned
 * 6. Lưu lại
 *
 * KHÔNG hardcode API Secret trong ứng dụng Android.
 */
public class CloudinaryUploader {

    // Cloud name của bạn
    private static final String CLOUD_NAME = "dukolxf6";

    // Unsigned Upload Preset - Bạn cần tạo preset này trên Cloudinary Dashboard
    // Settings > Upload > Upload presets > Add upload preset (Unsigned)
    private static final String UPLOAD_PRESET = "music_unsigned_preset";

    // URL upload Cloudinary
    private static final String IMAGE_UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";
    private static final String RAW_UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/raw/upload";

    private final OkHttpClient okHttpClient;
    private final Handler mainHandler;

    public interface UploadCallback {
        void onSuccess(String url);
        void onError(String errorMessage);
    }

    public CloudinaryUploader() {
        okHttpClient = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Upload ảnh nền bài hát lên Cloudinary (dạng image).
     *
     * @param context  Context của Activity
     * @param imageUri Uri của file ảnh được chọn
     * @param callback Callback trả về URL hoặc lỗi
     */
    public void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        uploadFile(context, imageUri, IMAGE_UPLOAD_URL, "image/jpeg", callback);
    }

    /**
     * Upload file mp3 lên Cloudinary (dạng raw).
     *
     * @param context  Context của Activity
     * @param mp3Uri   Uri của file mp3 được chọn
     * @param callback Callback trả về URL hoặc lỗi
     */
    public void uploadMp3(Context context, Uri mp3Uri, UploadCallback callback) {
        uploadFile(context, mp3Uri, RAW_UPLOAD_URL, "audio/mpeg", callback);
    }

    private void uploadFile(Context context, Uri fileUri, String uploadUrl,
                            String mimeType, UploadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                    if (inputStream == null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("Không thể đọc file. Vui lòng thử lại.");
                            }
                        });
                        return;
                    }

                    byte[] fileBytes = readInputStream(inputStream);
                    inputStream.close();

                    RequestBody fileBody = RequestBody.create(fileBytes, MediaType.parse(mimeType));

                    MultipartBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", "upload_file", fileBody)
                            .addFormDataPart("upload_preset", UPLOAD_PRESET)
                            .build();

                    Request request = new Request.Builder()
                            .url(uploadUrl)
                            .post(requestBody)
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError("Lỗi kết nối: " + e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String responseBody = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    String secureUrl = jsonObject.getString("secure_url");
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(secureUrl);
                                        }
                                    });
                                } catch (Exception e) {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError("Lỗi xử lý phản hồi: " + e.getMessage());
                                        }
                                    });
                                }
                            } else {
                                String errorBody = "";
                                try {
                                    errorBody = response.body().string();
                                } catch (Exception ignored) {}
                                final String finalErrorBody = errorBody;
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError("Upload thất bại (HTTP " + response.code() + "): " + finalErrorBody);
                                    }
                                });
                            }
                        }
                    });

                } catch (IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("Lỗi đọc file: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Đọc InputStream thành byte[] tương thích với minSdk 23 (Android 6.0+).
     * Thay thế cho inputStream.readAllBytes() chỉ có từ API 26.
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[16384];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
