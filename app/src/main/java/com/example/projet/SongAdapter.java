package com.example.projet;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songList;
    private LayoutInflater inflater;
    private SoundManager soundManager;
    private SharedPreferences sharedPreferences;

    public SongAdapter(Context context, List<Song> songList, SoundManager soundManager) {
        this.context = context;
        this.songList = songList;
        this.soundManager = soundManager;
        this.inflater = LayoutInflater.from(context);
        this.sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_song, parent, false);
        }

        // Récupération des éléments du layout
        TextView songTitle = convertView.findViewById(R.id.songTitle);
        ImageButton playButton = convertView.findViewById(R.id.playButton);
        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);

        Song currentSong = songList.get(position);
        songTitle.setText(currentSong.getTitle());

        // Vérifier si la chanson est déjà dans les favoris
        boolean isFavorite = sharedPreferences.getBoolean(currentSong.getTitle(), false);
        favoriteButton.setSelected(isFavorite);

        // Gestion du clic pour jouer le son
        playButton.setOnClickListener(v -> {
            soundManager.playSound(currentSong.getSoundName()); // Joue le son spécifique
        });

        // Gestion du clic sur le bouton des favoris
        favoriteButton.setOnClickListener(v -> {
            boolean isSelected = !v.isSelected();
            v.setSelected(isSelected); // Met à jour l'état sélectionné

            if (isSelected) {
                // Ajouter à SharedPreferences
                sharedPreferences.edit().putBoolean(currentSong.getTitle(), true).apply();
                Toast.makeText(context, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
            } else {
                // Retirer de SharedPreferences
                sharedPreferences.edit().remove(currentSong.getTitle()).apply();
                Toast.makeText(context, "Retiré des favoris", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
