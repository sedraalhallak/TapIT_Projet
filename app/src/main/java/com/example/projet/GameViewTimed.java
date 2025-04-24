package com.example.projet;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.SurfaceView;

public class GameViewTimed extends SurfaceView implements Runnable {
    // même logique que GameView.java...
    private boolean isRunning = false;
    private long startTime;
    private final long timeLimit = 60000; // 60s
    private Thread gameThread;

    public GameViewTimed(Context context, int screenWidth, int screenHeight) {
        super(context);
        // setup paint, tiles, etc.
    }

    public void startGame() {
        isRunning = true;
        startTime = System.currentTimeMillis();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= timeLimit) {
                stopGame();
                showGameOver(); // ou via un listener
                break;
            }

            // update tiles
            // draw on canvas
        }
    }

    private void showGameOver() {
        ((Activity) getContext()).runOnUiThread(() -> {
            Intent intent = new Intent(getContext(), GameOverDialog.class);
            getContext().startActivity(intent);
        });
    }
}
