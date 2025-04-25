package com.example.projet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet.Song;
import com.example.projet.SoundManager;

import java.util.List;
import java.util.Map;

public class SongAdapter extends ArrayAdapter<Song> {
    private Context context;
    private List<Song> songs;
    private SoundManager soundManager;
    private Map<String, Integer> songScores;

    public SongAdapter(Context context, List<Song> songs, SoundManager soundManager, Map<String, Integer> songScores) {
        super(context, 0, songs);
        this.context = context;
        this.songs = songs;
        this.soundManager = soundManager;
        this.songScores = songScores;
    }

    public void updateScores(Map<String, Integer> newScores) {
        this.songScores = newScores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_song, parent, false);
        }

        Song song = songs.get(position);

        TextView titleTextView = convertView.findViewById(R.id.songTitle);
        TextView artistTextView = convertView.findViewById(R.id.songArtist);
        TextView scoreTextView = convertView.findViewById(R.id.songHighScore);
        TextView starScoreView = convertView.findViewById(R.id.songStars); // Nouveau TextView pour étoiles
        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);
        ImageButton playButton = convertView.findViewById(R.id.playButton);

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());


        int highScore = songScores.containsKey(song.getTitle()) ? songScores.get(song.getTitle()) : 0;
        scoreTextView.setText(
                highScore > 0
                        ? context.getString(R.string.best_score, highScore)
                        : context.getString(R.string.not_played_yet)
        );


        String stars = "☆☆☆";
        if (highScore >= 6) {
            stars = "★★★";
        } else if (highScore >= 4) {
            stars = "★★☆";
        } else if (highScore >= 2) {
            stars = "★☆☆";
        }
        starScoreView.setText(stars);



        if (FavoriteManager.isFavorite(context, song)) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_pressed);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }

        favoriteButton.setOnClickListener(v -> {
            if (FavoriteManager.isFavorite(context, song)) {
                FavoriteManager.removeFavorite(context, song);
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(context, "Retiré des favoris : " + song.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                FavoriteManager.addFavorite(context, song);
                favoriteButton.setImageResource(R.drawable.ic_favorite_pressed);
                Toast.makeText(context, "Ajouté aux favoris : " + song.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Play
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        playButton.setBackgroundResource(outValue.resourceId);
        playButton.setClickable(true);
        playButton.setFocusable(true);

        playButton.setOnClickListener(v -> {
            try {
                String baseUrl = "http://10.0.2.2:8000/song_files/";
                String songUrl = baseUrl + song.getFilename();

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("song_title", song.getTitle());
                intent.putExtra("song_artist", song.getArtist());
                intent.putExtra("song_filename", song.getFilename());
                intent.putExtra("song_url", songUrl);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Erreur lancement activité: " + e.getMessage());
            }
        });

        return convertView;
    }
}