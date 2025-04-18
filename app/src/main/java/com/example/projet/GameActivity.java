package com.example.projet;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("song_filename")) {
            finish();
            return;
        }

        String songFilename = intent.getStringExtra("song_filename");
        if (songFilename == null) {
            finish();
            return;
        }

        try {
            String songUrl = "http://192.168.0.49:8000/song_files/" + songFilename;
            mediaPlayer = MediaPlayer.create(this, Uri.parse(songUrl));
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "Erreur de chargement de la musique", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Libérer les ressources audio
        }
    }
}