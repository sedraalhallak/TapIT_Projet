package com.example.projet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

public class SongAdapter extends ArrayAdapter<Song> {
    private Context context;
    private List<Song> songs;
    private SoundManager soundManager;

    public SongAdapter(Context context, List<Song> songs, SoundManager soundManager) {
        super(context, 0, songs);
        this.context = context;
        this.songs = songs;
        this.soundManager = soundManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_song, parent, false);
        }

        Song song = songs.get(position);

        TextView titleTextView = convertView.findViewById(R.id.songTitle);
        TextView artistTextView = convertView.findViewById(R.id.songArtist);
        //Button favoriteButton = convertView.findViewById(R.id.favoriteButton);
        Button playButton = convertView.findViewById(R.id.playButton);

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