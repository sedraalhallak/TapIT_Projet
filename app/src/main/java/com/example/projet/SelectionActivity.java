package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "L'activité " + getClass().getSimpleName() + " a été lancée.");
        setContentView(R.layout.activity_selection);

        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        // Initialisation des boutons
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        // Appliquer l'animation et démarrer l'activité correspondante
        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            setActiveButton(homeButton); // Mettre homeButton comme actif
            Intent homeIntent = new Intent(SelectionActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish(); // Fermer l'activité actuelle
        });

        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            setActiveButton(musicButton); // Mettre musicButton comme actif
            Toast.makeText(this, "Music", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            setActiveButton(settingsButton); // Mettre settingsButton comme actif
            startActivity(new Intent(SelectionActivity.this, SettingsActivity.class)); // Démarrer SettingsActivity
            finish();
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            setActiveButton(favoriteButton); // Mettre favoriteButton comme actif
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
        });

        // Mettre à jour l'état actif de chaque bouton après l'animation
        setActiveButton(musicButton); // Exemple : mettre musicButton comme bouton actif

        // Login button listener
        ImageButton loginIcon = findViewById(R.id.loginIcon);
        loginIcon.setOnClickListener(v -> {
            Intent loginIntent = new Intent(SelectionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    // Méthode pour définir l'état actif de chaque bouton
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
                button.setAlpha(0.85f);
                button.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }
}
