package com.example.magentastreaming;

public class Liked {

    private String userID;
    private String songID;

    public Liked(String userID, String songID) {
        this.userID = userID;
        this.songID = songID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }
}
