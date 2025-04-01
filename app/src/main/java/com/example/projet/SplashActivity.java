package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Charger le GIF
        ImageView gifImage = findViewById(R.id.gifImage);
        Glide.with(this).load(R.drawable.mon_gif).into(gifImage);
        // Charger le deuxième GIF (en haut)
        ImageView gifTop = findViewById(R.id.gifTop);
        Glide.with(this).load(R.drawable.gif2).into(gifTop);


        // Récupérer tout le layout (écran complet)
        ConstraintLayout splashLayout = findViewById(R.id.splash_layout);

        // Ajouter un OnClickListener sur tout l'écran
        splashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(); // Lancer le jeu quand l'utilisateur clique n'importe où
            }
        });
    }

    // Méthode pour démarrer le jeu
    private void startGame() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Ferme l'écran du menu après avoir lancé le jeu
    }
}
