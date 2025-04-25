package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
public class PauseActivity extends BaseActivity {
    private Button resumeButton;
    private Button restartButton;
    private Button homeButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        resumeButton = findViewById(R.id.resume_button);
        restartButton = findViewById(R.id.restart_button);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);

        applyClickEffect(resumeButton);
        applyClickEffect(restartButton);
        applyClickEffect(homeButton);
        applyClickEffect(settingsButton);



        resumeButton.setOnClickListener(v -> finish());


        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(PauseActivity.this, SettingsActivity.class);
            startActivity(settingsIntent); // Démarre l'activité des paramètres
        });



    }

    private void applyClickEffect(Button button) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Effet de rétrécissement
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        button,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                );
                scaleDown.setDuration(100);
                scaleDown.start();


                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Pour Android 8.0 (API 26+) et plus récent
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Pour les versions plus anciennes
                        vibrator.vibrate(50);
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // Retour à la taille normale
                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        button,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                );
                scaleUp.setDuration(100);
                scaleUp.start();
            }
            return false;
        });
    }

}
