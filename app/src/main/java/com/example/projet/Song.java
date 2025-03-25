package com.example.projet;

public class Song {
    private String title;
    private String soundName;

    public Song(String title, String soundName) {
        this.title = title;
        this.soundName = soundName;
    }

    public String getTitle() {
        return title;
    }

    public String getSoundName() {
        return soundName;
    }
}

