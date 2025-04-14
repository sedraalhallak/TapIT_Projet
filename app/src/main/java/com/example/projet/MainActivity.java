package com.example.projet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new GameView(this, size.x, size.y);
        String currentLang = LanguageUtils.getSavedLanguage(this);
        LanguageUtils.setLocale(this, currentLang);
        setContentView(gameView);
        /*
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String lang = prefs.getString("app_language", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());*/


        // Mettre en plein écran
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        setContentView(gameView);

        // Autoriser les connexions réseau sur le thread principal pour des fins de test
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
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