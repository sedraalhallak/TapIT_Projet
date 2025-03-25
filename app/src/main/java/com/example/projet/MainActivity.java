package com.example.projet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mettre en plein écran
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.stopGame();
    }
}
