package com.example.projet;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ArtistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        // Pour test : afficher le genre reçu
        String genre = getIntent().getStringExtra("genre");
        TextView textView = findViewById(R.id.genreTextView);
        textView.setText("Genre sélectionné : " + genre);
    }
}

