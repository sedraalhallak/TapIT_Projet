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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private VideoView videoView;
    private ListView songListView;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private SoundManager soundManager;
    private BroadcastReceiver profileUpdateReceiver;

    // Ajout pour la gestion des scores
    private Map<String, Integer> songScores = new HashMap<>();

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
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);  // Active la boucle infinie
            mp.setVolume(0f, 0f); // Désactive le son (optionnel)
            mp.start();           // Démarre la lecture
        });

        // Initialiser la liste des chansons
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songList, soundManager,songScores);
        songListView.setAdapter(songAdapter);

        // Charger les scores des chansons
        loadScores(); // Charge les scores depuis SharedPreferences

        // Appeler l'API pour récupérer les chansons
        new FetchSongsTask().execute();

        // Gestion des boutons de navigation
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String loggedInUser = prefs.getString("loggedInUsername", null);
                ImageView profileAvatar = findViewById(R.id.profileAvatar);
                profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
            }
        };

        homeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Déjà sur la page d'accueil", Toast.LENGTH_SHORT).show();
        });

        NavigationHelper.setupNavigationBar(this);
        setActiveButton(homeButton);

        musicButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectionActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        ImageView profileAvatar = findViewById(R.id.profileAvatar);

        // Charger l'avatar depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int avatarId = prefs.getInt("avatarId", R.drawable.a1);
        profileAvatar.setImageResource(avatarId);

        profileAvatar.setOnClickListener(v -> {
            String loggedInUser = prefs.getString("loggedInUsername", null);
            if (loggedInUser != null) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }

    // Méthodes pour gérer les scores
    private void loadScores() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);
        if (username != null) {
            SharedPreferences allScores = getSharedPreferences("SongScores", MODE_PRIVATE);
            Map<String, ?> allEntries = allScores.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().startsWith(username + "_")) {
                    String songTitle = entry.getKey().substring((username + "_").length());
                    songScores.put(songTitle, (Integer) entry.getValue());
                }
            }
        }
    }


    private void saveScores() {
        SharedPreferences.Editor editor = getSharedPreferences("SongScores", MODE_PRIVATE).edit();
        for (Map.Entry<String, Integer> entry : songScores.entrySet()) {
            editor.putInt(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public void updateSongScore(String title, int score) {
        if (score > (songScores.containsKey(title) ? songScores.get(title) : 0)) {
            songScores.put(title, score);
            saveScores(); // Sauvegarde immédiate
        }
    }

    public int getSongScore(String title) {
        return songScores.containsKey(title) ? songScores.get(title) : 0;
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ImageView profileAvatar = findViewById(R.id.profileAvatar);
        profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
        if (videoView != null) {
            videoView.seekTo(VideoManager.getInstance().getCurrentPosition());
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            VideoManager.getInstance().setCurrentPosition(videoView.getCurrentPosition());
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release();
        videoView.stopPlayback();
    }

    private void setActiveButton(LinearLayout activeButton) {
        LinearLayout[] buttons = {
                findViewById(R.id.homeButton),
                findViewById(R.id.musicButton),
                findViewById(R.id.favoriteButton),
                findViewById(R.id.settingsButton)
        };
        for (LinearLayout button : buttons) {
            button.setBackground(null);
            if (button == activeButton) {
                button.setAlpha(1.0f);
                button.setBackgroundResource(R.drawable.nav_button_background_selected);
            } else {
                button.setAlpha(0.85f);
                button.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }

    private class FetchSongsTask extends AsyncTask<Void, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Void... voids) {
            List<Song> songs = new ArrayList<>();
            try {
                URL url = new URL("http://10.0.2.2:8000/api/songs");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
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
                    Toast.makeText(HomeActivity.this, "Erreur serveur : " +
                            connection.getResponseCode(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, "Erreur connexion : " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            songList.clear();
            songList.addAll(songs);
            songAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null &&
                data.getBooleanExtra("avatarUpdated", false)) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            ImageView profileAvatar = findViewById(R.id.profileAvatar);
            profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
        }
    }
}