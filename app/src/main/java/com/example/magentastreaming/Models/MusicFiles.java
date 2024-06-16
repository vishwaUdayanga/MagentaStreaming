package com.example.magentastreaming.Models;

public class MusicFiles {
    private String songId;
    private String title;
    private String albumArt;
    private String albumName;
    private double duration;
    private String artist;
    private String genre;

    private String clip_source;

    public MusicFiles(String title, String albumArt, String albumName, double duration, String artist, String clip_source, String genre, String songId) {
        this.title = title;
        this.albumArt = albumArt;
        this.albumName = albumName;
        this.duration = duration;
        this.artist = artist;
        this.clip_source = clip_source;
        this.genre = genre;
        this.songId = songId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getClip_source() {
        return clip_source;
    }

    public void setClip_source(String clip_source) {
        this.clip_source = clip_source;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getAlbumName() {
        return albumName;
    }

    public double getDuration() {
        return duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
