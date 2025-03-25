package com.example.projet;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

public class SoundManager {
    private SoundPool soundPool;
    private HashMap<String, Integer> soundMap; // Pour gérer plusieurs sons
    private MediaPlayer backgroundMusic;
    private float volume = 1.0f; // Volume par défaut

    public SoundManager(Context context) {
        // Initialisation des attributs audio
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5) // Permet plusieurs sons simultanés
                .setAudioAttributes(attributes)
                .build();

        soundMap = new HashMap<>();
        loadSounds(context);

        // Initialisation de la musique de fond
        backgroundMusic = MediaPlayer.create(context, R.raw.piano_note );  // Remplace par ton vrai fichier
        if (backgroundMusic != null) {
            backgroundMusic.setLooping(true);
        } else {
            Log.e("SoundManager", "Erreur : impossible de charger la musique de fond.");
        }
    }

    // Chargement des effets sonores
    private void loadSounds(Context context) {
        try {
            // Ajouter plusieurs sons si nécessaire
            //soundMap.put("piano_note1", soundPool.load(context, R.raw.piano_note1, 1));
            //soundMap.put("piano_note2", soundPool.load(context, R.raw.piano_note, 1)); // Remplace par le vrai fichier
        } catch (Exception e) {
            Log.e("SoundManager", "Erreur lors du chargement des sons : " + e.getMessage());
        }
    }

    // Jouer un son spécifique
    public void playSound(String soundName) {
        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            soundPool.play(soundId, volume, volume, 1, 0, 1f);
        } else {
            Log.e("SoundManager", "Le son " + soundName + " n'existe pas.");
        }
    }

    // Jouer la musique de fond
    public void playBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    // Mettre la musique de fond en pause
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    // Arrêter la musique de fond complètement
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
            try {
                backgroundMusic.prepare(); // Prépare pour une future lecture
            } catch (Exception e) {
                Log.e("SoundManager", "Erreur lors de la préparation de la musique : " + e.getMessage());
            }
        }
    }
    /*private void loadSounds(Context context) {
        try {
            // Ajoute les sons à ton HashMap
            soundMap.put("piano_note1", soundPool.load(context, R.raw.piano_note1, 1));
           // soundMap.put("piano_note2", soundPool.load(context, R.raw.piano_note2, 1));
           //soundMap.put("piano_note3", soundPool.load(context, R.raw.piano_note3, 1));
        } catch (Exception e) {
            Log.e("SoundManager", "Erreur lors du chargement des sons : " + e.getMessage());
        }
    }*/


    // Régler le volume
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(volume, 1)); // Volume entre 0 et 1
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(this.volume, this.volume);
        }
    }

    // Obtenir la position actuelle de la musique
    public int getCurrentMusicPosition() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            return backgroundMusic.getCurrentPosition();
        }
        return 0;
    }

    // Libérer les ressources
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}
