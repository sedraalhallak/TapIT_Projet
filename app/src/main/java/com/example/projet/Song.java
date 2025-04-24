package com.example.projet;

public class Song {
    private String title;
    private String artist;
    private String filename;
    private int highScore;


    public Song() {}

    public Song(String title, String artist, String filename) {
        this.title = title;
        this.artist = artist;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilename() {
        return filename;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}