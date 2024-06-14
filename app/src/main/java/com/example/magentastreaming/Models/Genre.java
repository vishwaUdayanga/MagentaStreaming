package com.example.magentastreaming.Models;

public class Genre {
    private String genreName;
    private String genreArt;

    private int ID;

    public Genre(String genreName, String genreArt,int ID) {
        this.genreName = genreName;
        this.genreArt = genreArt;
        this.ID =ID;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getGenreArt() {
        return genreArt;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setGenreArt(String genreArt) {
        this.genreArt = genreArt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
