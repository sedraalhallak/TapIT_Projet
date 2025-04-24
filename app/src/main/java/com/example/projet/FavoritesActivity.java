package com.example.projet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {
    private VideoView videoView;

    private ListView favoriteListView;
    private SongAdapter songAdapter;
    private List<Song> favoriteSongs;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Initialisation des vues
        videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.piano);
        videoView.setVideoURI(videoUri);
        videoView.start();

        // Charger les scores
        loadScores();

        Toast.makeText(this, "Page des favoris", Toast.LENGTH_SHORT).show();

        ListView favoriteListView = findViewById(R.id.favoriteListView);
        soundManager = new SoundManager(this);
        favoriteSongs = FavoriteManager.getFavorites(this);
        TextView emptyMessage = findViewById(R.id.emptyFavoritesMessage);

        if (favoriteSongs.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }

        // Passer les scores à l'adaptateur
        songAdapter = new SongAdapter(this, favoriteSongs, soundManager, songScores);
        favoriteListView.setAdapter(songAdapter);

        // Navigation
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        NavigationHelper.setupNavigationBar(this);
        setActiveButton(favoriteButton);
        ProfileUtils.setupProfileAvatar(this, R.id.profileAvatar);

        homeButton.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        musicButton.setOnClickListener(v -> startActivity(new Intent(this, SelectionActivity.class)));
        favoriteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Déjà sur la page des favoris", Toast.LENGTH_SHORT).show();
        });
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
    private Map<String, Integer> songScores = new HashMap<>();

    // Méthode pour charger les scores depuis SharedPreferences
    private void loadScores() {
        String username = getCurrentUsername();
        SharedPreferences prefs = getSharedPreferences("SongScores_" + username, MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            songScores.put(entry.getKey(), (Integer) entry.getValue());
        }
    }
    private String getCurrentUsername() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getString("username", "defaultUser");
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
    public class SelectionActivity extends AppCompatActivity {
        // ...

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_selection);

            // Initialiser l'avatar (remplacez R.id.profileAvatar par votre ID)
            ProfileUtils.setupProfileAvatar(this, R.id.profileAvatar);

            // ... reste de votre code
        }

        @Override
        protected void onResume() {
            super.onResume();

            // Rafraîchir l'avatar
            ImageView profileAvatar = findViewById(R.id.profileAvatar);
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));

            // Gestion spécifique de la vidéo
            if (!videoView.isPlaying()) {
                videoView.start();
            }
            loadScores(); // recharge les scores à chaque fois
            if (songAdapter != null) {
                songAdapter.updateScores(songScores); // tu dois ajouter cette méthode dans ton adapter
                songAdapter.notifyDataSetChanged();
            }

        }
    }



}

