package com.example.projet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Point;

public class MainActivity extends BaseActivity {
    private GameView gameView;
    private MediaPlayer mediaPlayer;
    private String songUrl;
    private int currentMusicPosition = 0; // Stocke la position actuelle de la musique
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String songTitle;
    private int highScore = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;


        Intent intent = getIntent();
        if (intent == null ||
                !intent.hasExtra("song_title") ||
                !intent.hasExtra("song_artist") ||
                !intent.hasExtra("song_filename") ||
                !intent.hasExtra("song_url")) {
            Log.e("DEBUG", "Données manquantes dans l'Intent");
            showErrorDialog("Erreur : Données manquantes");
            return;
        }

        // get songs inftomations
        songTitle = intent.getStringExtra("song_title");
        loadHighScore();

        songUrl = intent.getStringExtra("song_url");
        if (songUrl == null || songUrl.isEmpty()) {
            Log.e("DEBUG", "URL de la chanson invalide");
            showErrorDialog("Erreur : URL de la chanson invalide");
            return;
        }
        Log.d("DEBUG", "Données reçues : ");
        Log.d("DEBUG", "Titre : " + intent.getStringExtra("song_title"));
        Log.d("DEBUG", "Artiste : " + intent.getStringExtra("song_artist"));
        Log.d("DEBUG", "Nom du fichier : " + intent.getStringExtra("song_filename"));
        Log.d("DEBUG", "URL : " + intent.getStringExtra("song_url"));

        // Gameview
        gameView = new GameView(this, screenWidth, screenHeight);
        setContentView(gameView);

        loadAndPlayMusic();
    }




    public String getSongTitle() {

        return songTitle;

    }
    public GameView getGameView() {
        return gameView;
    }




    public int getSongHighScore() {

        return highScore;

    }




    public void updateSongHighScore(String title, int newScore) {
        if (newScore > highScore) {
            highScore = newScore;
            saveHighScore(title, newScore);
        }
    }




    private void loadHighScore() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);
        if (username != null) {
            SharedPreferences scoresPrefs = getSharedPreferences("SongScores", MODE_PRIVATE);
            String key = username + "_" + songTitle;
            highScore = scoresPrefs.getInt(key, 0);
        }
    }






    private void saveHighScore(String title, int score) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);
        if (username != null) {
            SharedPreferences.Editor editor = getSharedPreferences("SongScores", MODE_PRIVATE).edit();
            String key = username + "_" + title;
            editor.putInt(key, score);
            editor.apply();
        }
    }

    public int getStarsEarned() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);
        if (username != null && songTitle != null) {
            SharedPreferences starPrefs = getSharedPreferences("SongStars", MODE_PRIVATE);
            String key = username + "_" + songTitle;
            return starPrefs.getInt(key, 0);
        }
        return 0;
    }







    public boolean isValidUrl(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            return huc.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void loadAndPlayMusic() {
        executor.execute(() -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();

                mainHandler.post(() -> {
                    try {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                        gameView.startGame();
                    } catch (Exception e) {
                        Log.e("DEBUG", "Erreur lors de la lecture de la musique : " + e.getMessage());
                        showErrorDialog("Impossible de lire la musique. Veuillez réessayer.");
                    }
                });
            } catch (IOException e) {
                mainHandler.post(() -> {
                    Log.e("DEBUG", "Erreur d'accès au fichier MP3 : " + e.getMessage());
                    showErrorDialog("Fichier MP3 introuvable ou inaccessible.");
                });
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    private void showErrorDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
            builder.setTitle("Erreur");
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setCancelable(false);
            builder.show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (gameView != null) {
            gameView.stopGame();
        }
    }
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentMusicPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }
    public void restartMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(currentMusicPosition);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        executor.shutdownNow();
    }


    public long getCurrentMusicPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }
    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


