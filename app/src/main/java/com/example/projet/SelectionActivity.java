package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Button popButton = findViewById(R.id.pop_button);
        Button classicalButton = findViewById(R.id.classical_button);
        Button hipHopButton = findViewById(R.id.hiphop_button);
        Button rockButton = findViewById(R.id.rock_button);
        Button easyButton = findViewById(R.id.easy_button);
        Button normalButton = findViewById(R.id.normal_button);
        Button hardButton = findViewById(R.id.hard_button);

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

        View.OnClickListener difficultyClickListener = v -> {
            String difficulty = ((Button) v).getText().toString();
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("SELECTED_DIFFICULTY", difficulty);
            startActivity(intent);
        };

        easyButton.setOnClickListener(difficultyClickListener);
        normalButton.setOnClickListener(difficultyClickListener);
        hardButton.setOnClickListener(difficultyClickListener);
        // Gestion des boutons de navigation
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            // Si vous voulez revenir à HomeActivity, vous pouvez le faire comme suit :
            Intent homeIntent = new Intent(SelectionActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish(); // Terminer l'activité actuelle pour revenir à l'écran d'accueil
        });

        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, SelectionActivity.class);
            startActivity(intent);
        });
       // ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            // Démarrer l'activité des paramètres
            Intent settingsIntent = new Intent(SelectionActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });




        favoriteButton.setOnClickListener(v -> Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show());
    }
}
