package com.example.musicapp.models;

public class MusicModel {

    private String musicId;
    private String title;
    private String artist;
    private String category;
    private String description;
    private String imageUrl;
    private String mp3Url;
    private String createdAt;
    private String updatedAt;

    public MusicModel() {
        // Required empty constructor for Firebase
    }

    public MusicModel(String musicId, String title, String artist, String category,
                      String description, String imageUrl, String mp3Url,
                      String createdAt, String updatedAt) {
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.mp3Url = mp3Url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
