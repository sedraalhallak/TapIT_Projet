package com.example.projet;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Collections;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
public class SongSelectionActivity extends AppCompatActivity {
    private static final String API_BASE_URL = "http://192.168.0.49:8000/";

    private LinearLayout songsLayout;
    private Button testButton;
    private Song selectedSong;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "L'activité " + getClass().getSimpleName() + " a été lancée.");
        setContentView(R.layout.activity_song_selection);

        songsLayout = findViewById(R.id.songsLayout);
        testButton = findViewById(R.id.testButton);
        Button skipButton = findViewById(R.id.skipButton);
        /*skipButton.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            skipButton.setVisibility(View.VISIBLE);
            skipButton.setAlpha(0f);
            skipButton.animate().alpha(1f).setDuration(500);
        }, 3000);
        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(SongSelectionActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });*/
        skipButton.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            skipButton.setVisibility(View.VISIBLE);
            skipButton.setAlpha(0f);
            skipButton.animate().alpha(1f).setDuration(500);
        }, 3000);

// 🟣 OnClick avec animation + redirection
        skipButton.setOnClickListener(v -> {
            // 💥 Animation de clic
            Animation clickAnim = AnimationUtils.loadAnimation(SongSelectionActivity.this, R.anim.click_scale);
            skipButton.startAnimation(clickAnim);

            // ⏳ On attend un peu que l'effet se joue (100ms)
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SongSelectionActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }, 120);
        });


        errorText = findViewById(R.id.errorText);
        testButton.setEnabled(false);


        loadSongs();

        testButton.setOnClickListener(v -> {
            if (selectedSong != null && selectedSong.getFilename() != null && !selectedSong.getFilename().isEmpty()) {
                String songUrl = API_BASE_URL + "song_files/" + selectedSong.getFilename();
                if (isValidUrl(songUrl)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("song_url", songUrl);
                    intent.putExtra("song_title", selectedSong.getTitle());
                    intent.putExtra("song_artist", selectedSong.getArtist());
                    intent.putExtra("song_filename", selectedSong.getFilename());
                    startActivity(intent);
                } else {
                    showError("Erreur : URL invalide");
                }
            } else {
                showError("Erreur : Chanson invalide ou vide");
            }
        });
    }

    private void loadSongs() {
        Log.d("API", "Tentative de connexion à: " + API_BASE_URL);
        errorText.setText("Chargement des chansons...");
        errorText.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Song>> call = apiService.getSongs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body();
                    if (songs.isEmpty()) {
                        showError("Aucune chanson disponible");
                    } else {
                        displaySongs(songs);
                    }
                } else {
                    showError("Erreur de serveur: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                showError("Erreur de connexion: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void displaySongs(List<Song> songs) {
        runOnUiThread(() -> {
            songsLayout.removeAllViews();
            errorText.setVisibility(View.GONE);

            List<Song> randomSongs = getRandomSongs(songs, 6);

            for (Song song : randomSongs) {
                Button songButton = new Button(this);
                songButton.setText(song.getArtist() + " - " + song.getTitle());
                songButton.setTextColor(Color.WHITE);
                songButton.setBackgroundResource(R.drawable.song_item_background); // 🟢 applique le fond
                songButton.setAllCaps(false); // si tu veux garder les noms en minuscule/naturel

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 12, 0, 12); // espace entre les chansons
                songButton.setLayoutParams(params);

                songButton.setOnClickListener(v -> {
                    selectedSong = song;
                    testButton.setEnabled(true);
                    highlightSelectedButton(songButton);
                });

                songsLayout.addView(songButton);
            }

        });
    }

    private void highlightSelectedButton(Button selectedButton) {
        for (int i = 0; i < songsLayout.getChildCount(); i++) {
            View child = songsLayout.getChildAt(i);
            if (child instanceof Button) {
                child.setSelected(false); // désélectionner tous les boutons
            }
        }
        selectedButton.setSelected(true); // sélectionner le bon
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private List<Song> getRandomSongs(List<Song> songs, int count) {
        Collections.shuffle(songs);
        return songs.subList(0, Math.min(count, songs.size()));
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            errorText.setText(message);
            errorText.setVisibility(View.VISIBLE);
        });
    }
}
