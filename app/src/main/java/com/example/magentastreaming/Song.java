package com.example.magentastreaming;

public class Song {
    private String songName;
    private String songArt;
    private String songDuration;
    private String songArtist;

    public Song(String songName, String songArt, String songDuration,String songArtist) {
        this.songName = songName;
        this.songArt = songArt;
        this.songDuration = songDuration;
        this.songArtist =songArtist;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArt() {
        return songArt;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongArt(String songArt) {
        this.songArt = songArt;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }
}
