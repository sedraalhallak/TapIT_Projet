package com.example.projet;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class SoundManager {
    private MediaPlayer mediaPlayer;

    public SoundManager(Context context) {
        mediaPlayer = new MediaPlayer();
    }

    public void playSong(Uri songUri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songUri.toString());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}