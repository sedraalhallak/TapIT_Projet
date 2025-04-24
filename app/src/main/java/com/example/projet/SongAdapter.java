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


    public SongAdapter(Context context, List<Song> songs, SoundManager soundManager,Map<String, Integer> songScores) {
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

        TextView scoreTextView = convertView.findViewById(R.id.songHighScore); // Nouveau TextView pour le score
// Afficher le score
        int highScore = songScores.containsKey(song.getTitle()) ? songScores.get(song.getTitle()) : 0;        scoreTextView.setText(highScore > 0 ? "Meilleur: " + highScore : "Pas encore joué");


        TextView titleTextView = convertView.findViewById(R.id.songTitle);
        TextView artistTextView = convertView.findViewById(R.id.songArtist);
        //Button favoriteButton = convertView.findViewById(R.id.favoriteButton);
        // Button playButton = convertView.findViewById(R.id.playButton);

        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());

        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);

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




        ImageButton playButton = convertView.findViewById(R.id.playButton);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        playButton.setBackgroundResource(outValue.resourceId);
        playButton.setClickable(true);
        playButton.setFocusable(true);

        // Bouton Play
        // Modifiez le onClickListener du bouton play
        playButton.setOnClickListener(v -> {
            try {
                String baseUrl = "http://10.0.2.2:8000/song_files/";
                String songUrl = baseUrl + song.getFilename();

                // Vérifiez que les données sont valides
                if (song.getTitle() == null || song.getArtist() == null || song.getFilename() == null) {
                    Toast.makeText(context, "Erreur: données de chanson invalides", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("DEBUG", "Lancement de la chanson: " + song.getTitle());

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
        /*
        // Après avoir défini le titre et l'artiste
        TextView scoreTextView = convertView.findViewById(R.id.songHighScore);
        int highScore = song.getHighScore();
        scoreTextView.setText(highScore > 0 ? "Meilleur: "+highScore : "Pas encore joué");*/

        return convertView;
    }


    // ViewHolder class to improve performance
    private static class ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        Button favoriteButton;
        Button playButton;
    }
}