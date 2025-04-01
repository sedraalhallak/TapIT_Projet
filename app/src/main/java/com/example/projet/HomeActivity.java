package com.example.projet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import com.example.projet.SongAdapter;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private VideoView videoView;
    //  private ListView songListView;
    // private String[] songs = {"Hello - Adele", "Hometown Glory - Adele", "Easy On Me - Adele", "Monkey - Adele"};
    private ListView songListView;
    private List<Song> songList;
    private SoundManager soundManager;
    private SongAdapter songAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialisation des vues
        videoView = findViewById(R.id.videoView);
        // songListView = findViewById(R.id.songListView);
        // Initialisation du SoundManager
        soundManager = new SoundManager(this);
        // Charger et démarrer la vidéo
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.piano_video);
        videoView.setVideoURI(videoUri);

        soundManager.playBackgroundMusic(); // Lancer la musique de fond si nécessaire

        // Initialisation de la ListView
        songListView = findViewById(R.id.songListView);
        songList = new ArrayList<>();

        // Ajouter des chansons avec le nom du son dans le HashMap
        songList.add(new Song("Piano Note 1", "piano_note1"));
        songList.add(new Song("Piano Note 2", "piano_note2"));
        songList.add(new Song("Piano Note 3", "piano_note3"));

        // Initialiser l'adaptateur
        songAdapter = new SongAdapter(this, songList, soundManager);
        songListView.setAdapter(songAdapter);

        // Gestion des boutons de navigation
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());
        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SelectionActivity.class);
            startActivity(intent);
        });
        //ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            // Démarrer l'activité des paramètres
            Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        favoriteButton.setOnClickListener(v -> Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release(); // Libérer les ressources
    }
    @Override
    protected void onResume() {
        super.onResume();
        videoView.start(); // Redémarre la vidéo quand l'activité reprend
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause(); // Met en pause la vidéo quand l'activité est en pause
    }
}
