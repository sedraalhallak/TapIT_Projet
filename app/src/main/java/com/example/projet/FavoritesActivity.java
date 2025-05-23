package com.example.projet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
    private Map<String, Integer> songScores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);


        videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.piano);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            mp.start();
        });



        loadScores();

        favoriteListView = findViewById(R.id.favoriteListView);
        soundManager = new SoundManager(this);
        favoriteSongs = FavoriteManager.getFavorites(this);
        TextView emptyMessage = findViewById(R.id.emptyFavoritesMessage);

        if (favoriteSongs.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.GONE);
        }


        songAdapter = new SongAdapter(this, favoriteSongs, soundManager, songScores);
        favoriteListView.setAdapter(songAdapter);


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
    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            VideoManager.getInstance().setCurrentPosition(videoView.getCurrentPosition());
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        loadScores();

        if (songAdapter != null) {
            songAdapter.updateScores(songScores);
            songAdapter.notifyDataSetChanged();
        }

        if (videoView != null) {
            videoView.seekTo(VideoManager.getInstance().getCurrentPosition());
            videoView.start();
        }
    }

    private void loadScores() {
        SharedPreferences userPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = userPrefs.getString("loggedInUsername", "defaultUser");

        SharedPreferences prefs = getSharedPreferences("SongScores", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        songScores.clear();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(username + "_")) {
                String songTitle = entry.getKey().substring((username + "_").length());
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    songScores.put(songTitle, (Integer) value);
                }
            }
        }
    }


    private String getCurrentUsername() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getString("loggedInUsername", "defaultUser");
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
}
