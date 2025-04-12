package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import android.os.Handler;
import android.widget.TextView;
import android.view.animation.AlphaAnimation;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView catchyText = findViewById(R.id.catchyText);

    // Appliquer la police manuellement si elle est dans /assets/fonts/
            //Typeface typeface = Typeface.createFromAsset(getAssets(), "font/font.ttf");
            //catchyText.setTypeface(typeface);

    // Phrase à afficher
            String phrase = "🎵 Tap tap... Let the magic begin! 🎹";

    // Attendre un peu avant d'écrire
            new Handler().postDelayed(() -> {
                animateText(phrase, catchyText, 100); // 100ms par lettre
            }, 1000);
        // Charger le GIF
        ImageView gifImage = findViewById(R.id.gifImage);
        Glide.with(this).load(R.drawable.mon_gif).into(gifImage);
        /*Charger le deuxième GIF (en haut)
        ImageView gifTop = findViewById(R.id.gifTop);
        Glide.with(this).load(R.drawable.gif2).into(gifTop);*/


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
    private void animateText(String text, TextView textView, int delay) {
        final Handler handler = new Handler();
        textView.setText(""); // Clear the text
        for (int i = 0; i <= text.length(); i++) {
            final int index = i;
            handler.postDelayed(() -> {
                textView.setText(text.substring(0, index));
            }, delay * i);
        }
    }


    // Méthode pour démarrer le jeu
    private void startGame() {
        /*
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Ferme l'écran du menu après avoir lancé le jeu
        */
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
