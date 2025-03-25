package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PauseActivity extends Activity {
    private Button resumeButton;
    private Button restartButton;
    private Button homeButton;
    private Button settingsButton;  // Déclaration du bouton settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        resumeButton = findViewById(R.id.resume_button);
        restartButton = findViewById(R.id.restart_button);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);  // Initialiser le bouton settings

        // Bouton "Reprendre" : Ferme l'écran de pause et reprend le jeu
        resumeButton.setOnClickListener(v -> finish());

        // Bouton "Restart" : Redémarre le jeu complètement
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bouton "Home" : Retourne à l'écran d'accueil (SplashActivity)
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        // Bouton "Settings" : Ouvre l'écran des paramètres
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(PauseActivity.this, SettingsActivity.class);
            startActivity(settingsIntent); // Démarre l'activité des paramètres
        });



    }

}
