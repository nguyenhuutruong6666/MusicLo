package com.example.musiclo.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AudiusTrackResponse {
    @SerializedName("data")
    private List<AudiusTrack> data;

    public List<AudiusTrack> getData() {
        return data;
    }
    public void setData(List<AudiusTrack> data) {
        this.data = data;
    }
}
