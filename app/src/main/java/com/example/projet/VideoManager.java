package com.example.projet;


public class VideoManager {
    private static VideoManager instance;
    private int currentPosition = 0;

    private VideoManager() {}

    public static synchronized VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }
}