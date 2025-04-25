package com.example.projet;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/*
public class FavoriteManager {
    private static final String PREFS_NAME = "favorite_songs";
    private static final String KEY_FAVORITES = "favorites";

    public static void addFavorite(Context context, Song song) {
        List<Song> favorites = getFavorites(context);
        for (Song s : favorites) {
            if (s.getTitle().equals(song.getTitle()) &&
                    s.getArtist().equals(song.getArtist()) &&
                    s.getFilename().equals(song.getFilename())) {
                // Déjà dans les favoris
                return;
            }
        }
        favorites.add(song);
        saveFavorites(context, favorites);
    }



    public static List<Song> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITES, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Song>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }


    private static void saveFavorites(Context context, List<Song> favorites) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        editor.putString(KEY_FAVORITES, json);
        editor.apply();
    }
    public static void removeFavorite(Context context, Song song) {
        List<Song> favorites = getFavorites(context);
        favorites.removeIf(s ->
                s.getTitle().equals(song.getTitle()) &&
                        s.getArtist().equals(song.getArtist()) &&
                        s.getFilename().equals(song.getFilename())
        );
        saveFavorites(context, favorites);
    }

    public static boolean isFavorite(Context context, Song song) {
        List<Song> favorites = getFavorites(context);
        for (Song fav : favorites) {
            if (fav.getTitle().equals(song.getTitle()) &&
                    fav.getArtist().equals(song.getArtist()) &&
                    fav.getFilename().equals(song.getFilename())) {
                return true;
            }
        }
        return false;
    }


}*/

public class FavoriteManager {
    private static final String PREFS_NAME = "favorite_songs";

    private static String getUserKey(Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = userPrefs.getString("loggedInUsername", null);
        return username != null ? "favorites_" + username : null;
    }

    public static void addFavorite(Context context, Song song) {
        String userKey = getUserKey(context);
        if (userKey == null) return;

        List<Song> favorites = getFavorites(context);
        for (Song s : favorites) {
            if (s.getTitle().equals(song.getTitle()) &&
                    s.getArtist().equals(song.getArtist()) &&
                    s.getFilename().equals(song.getFilename())) {
                // Déjà dans les favoris
                return;
            }
        }
        favorites.add(song);
        saveFavorites(context, favorites, userKey);
    }

    public static List<Song> getFavorites(Context context) {
        String userKey = getUserKey(context);
        if (userKey == null) return new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(userKey, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Song>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    private static void saveFavorites(Context context, List<Song> favorites, String userKey) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        editor.putString(userKey, json);
        editor.apply();
    }

    public static void removeFavorite(Context context, Song song) {
        String userKey = getUserKey(context);
        if (userKey == null) return;

        List<Song> favorites = getFavorites(context);
        favorites.removeIf(s ->
                s.getTitle().equals(song.getTitle()) &&
                        s.getArtist().equals(song.getArtist()) &&
                        s.getFilename().equals(song.getFilename())
        );
        saveFavorites(context, favorites, userKey);
    }

    public static boolean isFavorite(Context context, Song song) {
        List<Song> favorites = getFavorites(context);
        for (Song fav : favorites) {
            if (fav.getTitle().equals(song.getTitle()) &&
                    fav.getArtist().equals(song.getArtist()) &&
                    fav.getFilename().equals(song.getFilename())) {
                return true;
            }
        }
        return false;
    }
}