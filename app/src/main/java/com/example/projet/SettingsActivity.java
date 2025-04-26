package com.example.projet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        NavigationHelper.setupNavigationBar(this);
        ProfileUtils.setupProfileAvatar(this, R.id.profileAvatar);

        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);
        Button changeLanguageBtn = findViewById(R.id.changeLanguageBtn);

        // language
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String currentLang = prefs.getString("locale", "fr");

        updateLanguageButtonText(changeLanguageBtn, currentLang);

        Button toggleMusicBtn = findViewById(R.id.toggle_music_btn);
        toggleMusicBtn.setOnClickListener(v -> {
            if (MusicManager.isPlaying) {
                MusicManager.pause();
                toggleMusicBtn.setText("Turn on");
            } else {
                MusicManager.start(this);
                toggleMusicBtn.setText("Turn off");
            }
        });

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        musicButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectionActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            // already in it
        });

        setActiveButton(settingsButton);

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        changeLanguageBtn.setOnClickListener(v -> {
            String newLang = currentLang.equals("fr") ? "en" : "fr";
            prefs.edit().putString("locale", newLang).apply();
            LanguageUtils.setLocale(this, newLang);

            updateLanguageButtonText(changeLanguageBtn, newLang);

            // ⚡ Afficher la notification
            showNotification("Langue changée", "Nouvelle langue : " + (newLang.equals("fr") ? "Français" : "English"));

            // ➡️ Delayed restart
            new android.os.Handler().postDelayed(() -> {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }, 500);
        });
    }

    private void updateLanguageButtonText(Button button, String language) {
        if ("fr".equals(language)) {
            button.setText("Français");
        } else {
            button.setText("English");
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        ImageView profileAvatar = findViewById(R.id.profileAvatar);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelId = "settings_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Settings Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}
