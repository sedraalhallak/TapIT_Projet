package com.example.projet;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
public class HomeActivity extends AppCompatActivity {

    private VideoView videoView;
    private ListView songListView;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentLang = LanguageUtils.getSavedLanguage(this);
        LanguageUtils.setLocale(this, currentLang);

        // Maintenant on définit le contenu
        setContentView(R.layout.activity_home);
        // Initialisation des vues
        videoView = findViewById(R.id.videoView);
        songListView = findViewById(R.id.songListView);
        soundManager = new SoundManager(this);

        // Charger la vidéo
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.piano);
        videoView.setVideoURI(videoUri);

        soundManager.playBackgroundMusic();

        // Liste des sons
        songList = new ArrayList<>();
        songList.add(new Song("Piano Note 1", "piano_note1"));
        songList.add(new Song("Piano Note 2", "piano_note2"));
        songList.add(new Song("Piano Note 3", "piano_note3"));

        songAdapter = new SongAdapter(this, songList, soundManager);
        songListView.setAdapter(songAdapter);

        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        // Boutons
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);

            // Met à jour les styles des boutons pour indiquer que "Home" est actif
            setActiveButton(homeButton);

            // Message de confirmation
            Toast.makeText(HomeActivity.this, "Déjà sur la page d'accueil", Toast.LENGTH_SHORT).show();
        });



        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(musicButton);
            startActivity(new Intent(HomeActivity.this, SelectionActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(settingsButton);
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(favoriteButton);
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
        });
        NavigationHelper.setupNavigationBar(this);
        setActiveButton(homeButton); // Active visuellement le bouton Home dès l’ouverture de l’activité

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release(); // Libérer les ressources
    }


    private void setActiveButton(LinearLayout activeButton) {
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        List<LinearLayout> buttons = new ArrayList<>();
        buttons.add(homeButton);
        buttons.add(musicButton);
        buttons.add(favoriteButton);
        buttons.add(settingsButton);

        for (LinearLayout button : buttons) {
            button.setBackground(null); // enlève tout résidu visuel

            if (button == activeButton) {
                button.setAlpha(1.0f);
                button.setBackgroundResource(R.drawable.nav_button_background_selected);
            } else {
                button.setAlpha(0.85f);
                button.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }




}

