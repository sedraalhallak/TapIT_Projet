package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private SeekBar volumeSeekBar;
    private CheckBox muteCheckBox;
    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;
    private TextView settingsTitle;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Trouver les boutons de navigation
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        // Définir les actions de clic pour chaque bouton
        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish(); // Fermer l'activité actuelle
        });

        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SelectionActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v ->
                Toast.makeText(SettingsActivity.this, "You are already in Settings", Toast.LENGTH_SHORT).show()
        );

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Initialiser les composants UI
        settingsTitle = findViewById(R.id.settings_title);
        volumeSeekBar = findViewById(R.id.volume_slider);
        muteCheckBox = findViewById(R.id.mute_checkbox);
        languageSpinner = findViewById(R.id.language_spinner);

        // Configurer le MediaPlayer avec un fichier audio
        mediaPlayer = MediaPlayer.create(this, R.raw.piano_note1);  // Assurez-vous que le fichier music.mp3 est dans res/raw
        mediaPlayer.setLooping(true); // Répéter la musique
        mediaPlayer.start();

        // Initialiser AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = sharedPreferences.getInt("volume", maxVolume / 2); // Volume par défaut à 50%

        // Configurer la SeekBar de volume
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    mediaPlayer.setVolume(progress / (float) maxVolume, progress / (float) maxVolume);
                    sharedPreferences.edit().putInt("volume", progress).apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configurer la case à cocher pour couper le son
        muteCheckBox.setChecked(sharedPreferences.getBoolean("isMuted", false));
        muteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mediaPlayer.setVolume(0, 0);
            } else {
                int volume = volumeSeekBar.getProgress();
                mediaPlayer.setVolume(volume / (float) maxVolume, volume / (float) maxVolume);
            }
            sharedPreferences.edit().putBoolean("isMuted", isChecked).apply();
        });

        // Configurer le menu déroulant de la langue
        languageSpinner.setSelection(sharedPreferences.getInt("language", 0));
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sharedPreferences.edit().putInt("language", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Initialisation de l'affichage de la langue actuelle
        TextView currentLanguage = findViewById(R.id.current_language);
        currentLanguage.setOnClickListener(v -> {
            languageSpinner.setVisibility(View.VISIBLE);
            languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedLanguage = parentView.getItemAtPosition(position).toString();
                    currentLanguage.setText(selectedLanguage);
                    languageSpinner.setVisibility(View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
