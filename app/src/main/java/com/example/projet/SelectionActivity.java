package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Initialisation des boutons de genre
        Button popButton = findViewById(R.id.pop_button);
        Button classicalButton = findViewById(R.id.classical_button);
        Button hipHopButton = findViewById(R.id.hiphop_button);
        Button rockButton = findViewById(R.id.rock_button);

        // Initialisation des boutons de difficulté
        Button easyButton = findViewById(R.id.easy_button);
        Button normalButton = findViewById(R.id.normal_button);
        Button hardButton = findViewById(R.id.hard_button);

        // Listeners pour les genres
        View.OnClickListener genreClickListener = v -> {
            String genre = ((Button) v).getText().toString();
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("SELECTED_GENRE", genre);
            startActivity(intent);
        };

        popButton.setOnClickListener(genreClickListener);
        classicalButton.setOnClickListener(genreClickListener);
        hipHopButton.setOnClickListener(genreClickListener);
        rockButton.setOnClickListener(genreClickListener);

        // Listeners pour la difficulté
        View.OnClickListener difficultyClickListener = v -> {
            String difficulty = ((Button) v).getText().toString();
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("SELECTED_DIFFICULTY", difficulty);
            startActivity(intent);
        };

        easyButton.setOnClickListener(difficultyClickListener);
        normalButton.setOnClickListener(difficultyClickListener);
        hardButton.setOnClickListener(difficultyClickListener);

        // Animation
        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        // Initialisation des boutons de navigation
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        // Navigation
        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            Intent homeIntent = new Intent(SelectionActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            startActivity(new Intent(SelectionActivity.this, SelectionActivity.class));
            finish();
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            startActivity(new Intent(SelectionActivity.this, SettingsActivity.class));
            finish();
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
        });

        // Mettre à jour l’état actif du bouton navigation
        setActiveButton(musicButton);

        // Bouton login
        ImageButton loginIcon = findViewById(R.id.loginIcon);
        loginIcon.setOnClickListener(v -> {
            Intent loginIntent = new Intent(SelectionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    // Méthode pour définir l’état actif dans la barre de navigation
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
            TextView icon = (TextView) button.getChildAt(0); // icône
            if (button == activeButton) {
                icon.setBackgroundResource(R.drawable.nav_button_background_selected);
            } else {
                icon.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }
}
