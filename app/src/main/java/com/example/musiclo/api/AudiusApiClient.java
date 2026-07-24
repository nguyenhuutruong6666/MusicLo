package com.example.musiclo.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AudiusApiClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://api.audius.co/";
    private static final String BEARER_TOKEN = "Bearer tcefZoCfV47JRK8A5XSGZFRPI8yZ01Dj8LTFIAlatQA=";

    public static AudiusApiService getClient(String host) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", BEARER_TOKEN)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        if (host == null || host.isEmpty()) {
            host = BASE_URL;
        } else {
            if (!host.endsWith("/")) {
                host += "/";
            }
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AudiusApiService.class);
    }
}
