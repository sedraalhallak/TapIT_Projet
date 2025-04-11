package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

public class SignupActivity extends Activity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialisation des vues
        Button btnSignup = findViewById(R.id.btn_signup);
        TextView goToLogin = findViewById(R.id.go_to_login);

        // Détecteur de gestes pour le swipe
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Permet de capturer les événements de swipe
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // Classe interne pour détecter les swipes
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > 100) { // Seulement si la distance du swipe est assez grande
                if (diffX > 0) {
                    // Swipe vers la droite (Retour à LoginActivity)
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Swipe vers la gauche
                    // Tu pourrais mettre une autre activité ici si tu veux
                }
                return true;
            }
            return false;
        }
    }
}
