package com.example.projet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.projet.NavigationHelper;

public class HomeActivity extends AppCompatActivity {

    private VideoView videoView;
    private ListView songListView;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private SoundManager soundManager;
    private BroadcastReceiver profileUpdateReceiver;

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
        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        // Gestion des boutons de navigation
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                ImageView profileAvatar = findViewById(R.id.profileAvatar);
                profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
            }
        };

        homeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Déjà sur la page d'accueil", Toast.LENGTH_SHORT).show();
        });
        NavigationHelper.setupNavigationBar(this);
        setActiveButton(homeButton);  // pour appliquer le style "sélectionné" comme dans SelectionActivity


        musicButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectionActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            // Démarrer l'activité FavoritesActivity
            Intent intent = new Intent(HomeActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });


        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
        ImageView profileAvatar = findViewById(R.id.profileAvatar);

        // Charger l'avatar depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int avatarId = prefs.getInt("avatarId", R.drawable.a1); // Valeur par défaut
        profileAvatar.setImageResource(avatarId);

        // Gestion du clica
        //registerReceiver(profileUpdateReceiver, new IntentFilter("PROFILE_UPDATED"));

        profileAvatar.setOnClickListener(v -> {
            String loggedInUser = prefs.getString("loggedInUsername", null);
            if (loggedInUser != null) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir l'avatar à chaque retour sur l'écran
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ImageView profileAvatar = findViewById(R.id.profileAvatar);
        profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));

        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release(); // Libérer les ressources audio
        videoView.stopPlayback(); // Arrêter la vidéo
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


    // Classe interne pour récupérer les chansons depuis l'API
    private class FetchSongsTask extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            List<Song> songs = new ArrayList<>();
            try {
                URL url = new URL("http://10.0.2.2:8000/api/songs"); // Remplacez par votre URL API
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
    // Ajoutez cette méthode pour gérer le résultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("avatarUpdated", false)) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            ImageView profileAvatar = findViewById(R.id.profileAvatar);
            profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
        }
    }


}