package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        // Set the language based on saved preferences
        setLocale(getSavedLanguage());

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Views initialization
        TextView currentLanguageView = findViewById(R.id.current_language);
        languageSpinner = findViewById(R.id.language_spinner);
        volumeSeekBar = findViewById(R.id.volume_slider);
        muteCheckBox = findViewById(R.id.mute_checkbox);
        settingsTitle = findViewById(R.id.settings_title);

        // Make the current language label clickable
        currentLanguageView.setOnClickListener(v -> languageSpinner.performClick());

        /*// Language spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);*/

        // Set the current language in the spinner
        String currentLang = getSavedLanguage();
        int selectedIndex = currentLang.equals("fr") ? 1 : (currentLang.equals("es") ? 2 : 0);
        languageSpinner.setSelection(selectedIndex);

        // Language change listener
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = (position == 1) ? "fr" : "en";

                if (!selectedLang.equals(currentLang)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("app_language", selectedLang);
                    editor.apply();
                    setLocale(selectedLang);
                }

               /* // Update the current language display
                TextView currentLanguage = findViewById(R.id.current_language);
                if (currentLanguage != null) {
                    currentLanguage.setText(selectedLang.equals("fr") ? getString(R.string.french) : getString(R.string.english));
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Animation for buttons
        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);
        // Button listeners with animation
        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);

            // Met à jour les styles des boutons pour indiquer que "Home" est actif
            setActiveButton(homeButton);

            // Message de confirmation
            Toast.makeText(SettingsActivity.this, "Déjà sur la page d'accueil", Toast.LENGTH_SHORT).show();
        });


        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(musicButton);
            startActivity(new Intent(SettingsActivity.this, SelectionActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(settingsButton);
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(favoriteButton);
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
        });
        NavigationHelper.setupNavigationBar(this);
        // Audio settings (volume and mute)
        mediaPlayer = MediaPlayer.create(this, R.raw.piano_note1);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = sharedPreferences.getInt("volume", maxVolume / 2);

        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

        // Update volume when user interacts with the seekbar
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    mediaPlayer.setVolume(progress / (float) maxVolume, progress / (float) maxVolume);
                    sharedPreferences.edit().putInt("volume", progress).apply();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Mute checkbox listener
        muteCheckBox.setChecked(sharedPreferences.getBoolean("isMuted", false));
        muteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mute the audio
                mediaPlayer.setVolume(0, 0);
            } else {
                // Restore the volume based on the seekbar
                int volume = volumeSeekBar.getProgress();
                mediaPlayer.setVolume(volume / (float) maxVolume, volume / (float) maxVolume);
            }
            // Save mute state in shared preferences
            sharedPreferences.edit().putBoolean("isMuted", isChecked).apply();
        });

        // Login button listener
        ImageButton loginIcon = findViewById(R.id.loginIcon);
        loginIcon.setOnClickListener(v -> {
            Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        // Set active button for the navigation bar
        setActiveButton(findViewById(R.id.settingsButton)); // Mark 'Settings' button as active

        NavigationHelper.setupNavigationBar(this);
    }

    // Method to set locale based on the language code
    public void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        /*// Update UI elements with the new language
        settingsTitle = findViewById(R.id.settings_title);
        TextView langueLabel = findViewById(R.id.langue_label);
        TextView currentLanguage = findViewById(R.id.current_language);

        if (settingsTitle != null)
            settingsTitle.setText(getString(R.string.settings));
        if (langueLabel != null)
            langueLabel.setText(getString(R.string.language));
        if (currentLanguage != null)
            currentLanguage.setText(getString(R.string.current_language));*/
    }

    // Get the saved language from shared preferences
    private String getSavedLanguage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getString("app_language", "en");
    }

    // Method to set the active button in the navigation bar
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
            button.setBackground(null); // Réinitialise le fond précédent

            if (button == activeButton) {
                button.setAlpha(1.0f);
                button.setBackgroundResource(R.drawable.nav_button_background_selected);
            } else {
                button.setAlpha(0.4f);
                button.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
