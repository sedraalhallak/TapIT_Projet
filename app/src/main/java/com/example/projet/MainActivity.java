package com.example.projet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
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
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String lang = prefs.getString("app_language", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());


        // Mettre en plein écran
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        setContentView(gameView);

        // Autoriser les connexions réseau sur le thread principal pour des fins de test
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Tester la connexion à l'API Flask
        String response = getSongsFromAPI();
        if (response != null) {
            Toast.makeText(this, "Chansons récupérées: " + response, Toast.LENGTH_LONG).show();
        }
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

    // Méthode pour récupérer la liste des chansons depuis l'API Flask
    private String getSongsFromAPI() {
        try {
            // URL de ton API Flask (assure-toi que ton serveur Flask est bien lancé)
            URL url = new URL("http://127.0.0.1:5000/songs");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000); // Timeout de la connexion
            urlConnection.setReadTimeout(15000);    // Timeout de la lecture de la réponse

            // Lire la réponse de l'API
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString(); // Retourner la réponse sous forme de String
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retourner null en cas d'erreur
        }
    }
}
