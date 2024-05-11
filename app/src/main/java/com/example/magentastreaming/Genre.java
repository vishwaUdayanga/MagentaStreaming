package com.example.magentastreaming;

public class Genre {
    private String genreName;
    private String genreArt;

    public Genre(String genreName, String genreArt) {
        this.genreName = genreName;
        this.genreArt = genreArt;
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
}
