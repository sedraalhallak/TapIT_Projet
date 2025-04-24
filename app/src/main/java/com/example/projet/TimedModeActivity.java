package com.example.projet;

import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TimedModeActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private GameViewTimed gameView; // Utilise bien GameViewTimed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this); // si tu veux appliquer la langue ici aussi
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_mode);

        timerTextView = findViewById(R.id.timerTextView);
        FrameLayout gameContainer = findViewById(R.id.gameContainer);

        // Obtenir les dimensions de l'écran
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        // Créer GameViewTimed et l'ajouter dans le conteneur
        gameView = new GameViewTimed(this, screenWidth, screenHeight);
        gameContainer.addView(gameView); // ajout correct dans le layout
        gameView.startGame();

        // Timer de 60 secondes
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timerTextView.setText("Time's up!");
                gameView.stopGame();
                Toast.makeText(TimedModeActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                // Tu peux aussi appeler un écran GameOver ici
                finish();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        if (gameView != null) gameView.stopGame();
    }
}
