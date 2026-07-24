package com.example.musiclo.utils;

import com.example.musiclo.models.BaiHat;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private List<BaiHat> currentPlaylist;

    private MusicPlayerManager() {
        currentPlaylist = new ArrayList<>();
    }

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void setPlaylist(List<BaiHat> playlist) {
        this.currentPlaylist.clear();
        if (playlist != null) {
            this.currentPlaylist.addAll(playlist);
        }
    }

    public List<BaiHat> getPlaylist() {
        return currentPlaylist;
    }
}
