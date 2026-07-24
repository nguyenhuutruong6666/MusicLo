package com.example.musiclo.api;

import com.google.gson.annotations.SerializedName;

public class AudiusTrack {
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("genre")
    private String genre;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("artwork")
    private Artwork artwork;
    
    @SerializedName("user")
    private User user;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public Artwork getArtwork() { return artwork; }
    public User getUser() { return user; }

    public static class Artwork {
        @SerializedName("480x480")
        private String image480;
        
        public String getImage480() { return image480; }
    }

    public static class User {
        @SerializedName("name")
        private String name;
        
        public String getName() { return name; }
    }
}
