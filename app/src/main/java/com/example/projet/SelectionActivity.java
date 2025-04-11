package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
            Intent homeIntent = new Intent(SelectionActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish(); // Fermer l'activité actuelle
        });

        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            startActivity(new Intent(SelectionActivity.this, SelectionActivity.class)); // Retourner à l'écran de sélection
            finish();
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
            startActivity(new Intent(SelectionActivity.this, SettingsActivity.class)); // Démarrer SettingsActivity
            finish();
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation); // Appliquer l'animation
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
            TextView icon = (TextView) button.getChildAt(0); // icône du bouton
            if (button == activeButton) {
                icon.setBackgroundResource(R.drawable.nav_button_background_selected); // Icône sélectionnée
            } else {
                icon.setBackgroundResource(R.drawable.nav_button_background); // Icône par défaut
            }
        }
    }
}
