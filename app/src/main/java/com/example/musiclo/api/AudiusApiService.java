package com.example.musiclo.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AudiusApiService {
    @GET
    Call<AudiusResponse> getHosts(@Url String url);

    @GET("v1/tracks/trending")
    Call<AudiusTrackResponse> getTrendingTracks(@Query("app_name") String appName);
    
    @GET("v1/tracks/search")
    Call<AudiusTrackResponse> searchTracks(@Query("query") String query, @Query("app_name") String appName);
}
