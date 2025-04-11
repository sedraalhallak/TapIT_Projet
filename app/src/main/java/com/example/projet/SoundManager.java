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
    private boolean isMuted = false; // Indicateur du mute global

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
        loadSounds(context); // Charge les sons

        // Initialisation de la musique de fond
        backgroundMusic = MediaPlayer.create(context, R.raw.piano_note);  // Remplace par ton vrai fichier
        if (backgroundMusic != null) {
            backgroundMusic.setLooping(true);
        } else {
            Log.e("SoundManager", "Erreur : impossible de charger la musique de fond.");
        }
    }

    // Chargement des effets sonores
    private void loadSounds(Context context) {
        try {
            // Ajouter des sons à votre HashMap ici
            soundMap.put("piano_note1", soundPool.load(context, R.raw.piano_note1, 1));
            // Ajouter d'autres sons si nécessaire
        } catch (Exception e) {
            Log.e("SoundManager", "Erreur lors du chargement des sons : " + e.getMessage());
        }
    }

    // Jouer un son spécifique
    public void playSound(String soundName) {
        if (isMuted) return;  // Si mute est activé, ne pas jouer de son

        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            soundPool.play(soundId, volume, volume, 1, 0, 1f);
        } else {
            Log.e("SoundManager", "Le son " + soundName + " n'existe pas.");
        }
    }

    // Jouer la musique de fond
    public void playBackgroundMusic() {
        if (isMuted) return;  // Si mute est activé, ne pas jouer la musique de fond

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

    // Régler le volume
    public void setVolume(float volume) {
        if (isMuted) return;  // Si mute est activé, ne pas changer le volume

        this.volume = Math.max(0, Math.min(volume, 1)); // Volume entre 0 et 1
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(this.volume, this.volume);
        }
    }

    // Activer/Désactiver le mute global
    public void setMute(boolean mute) {
        isMuted = mute;
        if (isMuted) {
            // Couper tous les sons
            if (backgroundMusic != null) {
                backgroundMusic.setVolume(0, 0);
            }
            // Mettre en sourdine tous les effets sonores
            soundPool.autoPause(); // Met en pause tous les sons de SoundPool
        } else {
            // Restaurer le volume par défaut
            setVolume(volume);  // Utilise le volume défini précédemment
            // Reprendre la musique de fond si elle est en pause
            if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
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
