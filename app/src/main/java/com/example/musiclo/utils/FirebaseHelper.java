package com.example.musiclo.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static final String DATABASE_URL = "https://musicapp-420f7-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private static FirebaseHelper instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;

    private FirebaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public String getCurrentUid() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public DatabaseReference getUsersRef() {
        return firebaseDatabase.getReference("users");
    }

    public DatabaseReference getSongsRef() {
        return firebaseDatabase.getReference("songs");
    }

    public DatabaseReference getFavoritesRef() {
        return firebaseDatabase.getReference("favorites");
    }

    public DatabaseReference getUserRef(String uid) {
        return firebaseDatabase.getReference("users").child(uid);
    }

    public DatabaseReference getSongRef(String musicId) {
        return firebaseDatabase.getReference("songs").child(musicId);
    }

    public DatabaseReference getFavoriteRef(String uid, String musicId) {
        return firebaseDatabase.getReference("favorites").child(uid).child(musicId);
    }

    public DatabaseReference getUserFavoritesRef(String uid) {
        return firebaseDatabase.getReference("favorites").child(uid);
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
