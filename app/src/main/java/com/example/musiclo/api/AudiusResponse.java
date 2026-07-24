package com.example.musiclo.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AudiusResponse {
    @SerializedName("data")
    private List<String> hosts;

    public List<String> getHosts() {
        return hosts;
    }
    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }
}
