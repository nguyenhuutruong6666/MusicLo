package com.example.musicapp.models;

public class FavoriteModel {

    private String musicId;
    private String addedAt;

    public FavoriteModel() {
        // Required empty constructor for Firebase
    }

    public FavoriteModel(String musicId, String addedAt) {
        this.musicId = musicId;
        this.addedAt = addedAt;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}
