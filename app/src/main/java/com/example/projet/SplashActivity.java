package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Assure-toi que ce fichier XML est bien "activity_splash.xml"

        // Lance MainActivity après 3 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, 3000); // 3000 ms = 3 secondes

        // Ajouter un OnClickListener sur tout l'écran pour démarrer immédiatement le jeu
        findViewById(R.id.splash_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(); // Appel de la méthode qui lance le jeu
            }
        });
    }

    // Méthode pour démarrer MainActivity
    private void startGame() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Ferme SplashActivity
    }
}
