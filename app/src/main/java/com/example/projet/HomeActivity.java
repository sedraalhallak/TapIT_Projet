package com.example.projet;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class HomeActivity extends AppCompatActivity {

    private VideoView videoView;
    private ListView songListView;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "L'activité " + getClass().getSimpleName() + " a été lancée.");
        setContentView(R.layout.activity_home);

        // Initialisation des vues
        videoView = findViewById(R.id.videoView);
        songListView = findViewById(R.id.songListView);
        soundManager = new SoundManager(this);

        // Charger la vidéo en arrière-plan
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.piano);
        videoView.setVideoURI(videoUri);
        videoView.start();

        // Initialiser la liste des chansons
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songList, soundManager);
        songListView.setAdapter(songAdapter);

        // Appeler l'API pour récupérer les chansons
        new FetchSongsTask().execute();

        // Gestion des boutons de navigation
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Déjà sur la page d'accueil", Toast.LENGTH_SHORT).show();
        });

        musicButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectionActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Favoris", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            videoView.start(); // Redémarrer la vidéo si elle est en pause
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause(); // Mettre la vidéo en pause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release(); // Libérer les ressources audio
        videoView.stopPlayback(); // Arrêter la vidéo
    }

    // Classe interne pour récupérer les chansons depuis l'API
    private class FetchSongsTask extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            List<Song> songs = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.0.49:8000/api/songs"); // Remplacez par votre URL API
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(result.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject songObject = jsonArray.getJSONObject(i);
                        String title = songObject.getString("title");
                        String artist = songObject.getString("artist");
                        String filename = songObject.getString("filename");
                        songs.add(new Song(title, artist, filename));
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Erreur de serveur : " + connection.getResponseCode(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, "Erreur de connexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            songList.clear();
            songList.addAll(songs);
            songAdapter.notifyDataSetChanged(); // Rafraîchir la ListView
        }

    }
}