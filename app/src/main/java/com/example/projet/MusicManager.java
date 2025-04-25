package com.example.projet;


import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    static boolean isPlaying = false;
    static boolean shouldPlay = true; // Nouveau flag global

    public static void start(Context context) {
        if (!shouldPlay) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Arrêt forcé si shouldPlay est false
            }
            return;
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1f, 1f);
        }

        if (!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public static void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    // Nouvelle méthode pour contrôler le comportement
    public static void setShouldPlay(boolean play) {
        shouldPlay = play;
        if (!play && isPlaying) {
            pause();
        }
    }
}