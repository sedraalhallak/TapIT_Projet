package com.example.projet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import com.example.projet.Tile;


public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private SurfaceHolder holder;
    private Paint paint;
    private ArrayList<Tile> tiles;
    private int screenWidth, screenHeight;
    private Random random;
    private SoundManager soundManager;
    private int tileWidth;
    private int[] lastTileY;
    private int score;
    private Bitmap background;
    private Bitmap tileImage;
    private Bitmap pauseButtonImage;
    private Bitmap redTileImage;  // Image pour la tuile rouge
    private Tile lastMissedTile = null; // Dernière tuile ratée

    private long lastSpeedIncreaseTime = 0;
    private int tileSpeed = 15;
    private final int SPEED_INCREASE_INTERVAL = 3000;
    int tileHeight = 450; // Augmenter la hauteur pour que ce soit plus rectangulaire
    private Bitmap star;
    private Bitmap filledStar;
    // Variables globales pour le feedback
    private String feedbackText = "";
    private long feedbackStartTime = 0;
    private final int FEEDBACK_DURATION = 1000; // Durée en ms (1 seconde)
    public GameView(Context context, int width, int height) {
        super(context);
        screenWidth = width;
        screenHeight = height;
        holder = getHolder();
        paint = new Paint();
        tiles = new ArrayList<>();
        random = new Random();
        soundManager = new SoundManager(context);
        tileWidth = (int)(screenWidth / 3.2);
        lastTileY = new int[4];
        score = 0;

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_tiles);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);

        tileImage = BitmapFactory.decodeResource(getResources(), R.drawable.tile_button);
        tileImage = Bitmap.createScaledBitmap(tileImage, tileWidth, tileHeight, false);

        pauseButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        pauseButtonImage = Bitmap.createScaledBitmap(pauseButtonImage, 100, 100, false);

        redTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.red_tile_image);
        redTileImage = Bitmap.createScaledBitmap(redTileImage, tileWidth, tileHeight, false);

        soundManager.playBackgroundMusic();
        // Chargement et redimensionnement des étoiles UNE SEULE FOIS
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        star = Bitmap.createScaledBitmap(star, 100, 100, false);

        filledStar = BitmapFactory.decodeResource(getResources(), R.drawable.filled_star);
        filledStar = Bitmap.createScaledBitmap(filledStar, 100, 100, false);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void addTile() {
        int column = random.nextInt(4);  // Choisit une colonne aléatoire (0 à 3)

        // Calculer la position horizontale de la tuile
        int tileX = column * (screenWidth / 4); // Placer la tuile au début de la colonne
        int tileY = -300;  // Position initiale hors de l'écran (en haut)

        // Ajouter la tuile avec sa position X et Y
        tiles.add(new Tile(tileX, tileY, tileWidth, tileHeight));
        lastTileY[column] = tileY;  // Mettre à jour la dernière position Y pour cette colonne
    }


    private void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpeedIncreaseTime >= SPEED_INCREASE_INTERVAL) {
            tileSpeed += 2;
            lastSpeedIncreaseTime = currentTime;
        }

        Iterator<Tile> iterator = tiles.iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            tile.y += tileSpeed;

            if (tile.y + tile.height >= screenHeight) {
                lastMissedTile = tile;
                isPlaying = false;
                postInvalidate();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showGameOverDialog();
                return;
            }
        }

        if (shouldAddTile()) {
            addTile();
        }
    }

    private boolean shouldAddTile() {
        return tiles.isEmpty() || tiles.get(tiles.size() - 1).y > 400;
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawBitmap(background, 0, 0, null);

            for (Tile tile : tiles) {
                Bitmap tileBitmap = (tile == lastMissedTile) ? redTileImage : tileImage;
                canvas.drawBitmap(tileBitmap, tile.x, tile.y, null);
            }

            // Bouton de pause
            canvas.drawBitmap(pauseButtonImage, screenWidth - pauseButtonImage.getWidth() - 20, 20, null);

            // Affichage des étoiles, score et texte dynamique
            drawStars(canvas);
            drawScore(canvas);
            drawFeedback(canvas);

           // drawDynamicText(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }


    private void drawScore(Canvas canvas) {
        float scoreX = screenWidth / 2;
        float scoreY = 300; // Ajusté pour être en dessous des étoiles

        // Effet néon : Dessiner plusieurs couches avec une opacité dégradée
        Paint neonPaint = new Paint();
        neonPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        neonPaint.setTextSize(120);
        neonPaint.setColor(Color.WHITE);
        neonPaint.setTextAlign(Paint.Align.CENTER);
        neonPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER)); // Ajoute un flou lumineux

        // Plusieurs couches pour un effet plus prononcé
        for (int i = 6; i > 0; i--) {
            neonPaint.setAlpha(40 * i); // Plus on s'éloigne, plus c'est transparent
            canvas.drawText("" + score, scoreX, scoreY, neonPaint);
        }

        // Texte principal en blanc pur (sans effet flou)
        Paint mainPaint = new Paint();
        mainPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mainPaint.setTextSize(120);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("" + score, scoreX, scoreY, mainPaint);
    }






    private void drawStars(Canvas canvas) {
        int maxStars = 3;
        int filledStars = Math.min(score / 10, maxStars); // Maximum 3 étoiles

        // Définir des tailles différentes (Petite, Moyenne, Grande)
        int[] starSizes = {250, 250, 250}; // Ajustable selon ton besoin

        // Définir l'espacement dynamique entre les étoiles
        int spacing = 0;

        // Calcul de la largeur totale pour centrer correctement
        int totalWidth = starSizes[0] + starSizes[1] + starSizes[2] + (2 * spacing);
        int startX = (screenWidth / 2) - (totalWidth / 2);
        int starY = 30; // Ajuste la hauteur comme tu veux

        for (int i = 0; i < maxStars; i++) {
            int size = starSizes[i];

            // Sélectionner l’étoile remplie ou vide selon le score
            Bitmap resizedStar = Bitmap.createScaledBitmap(
                    (i < filledStars) ? filledStar : star, size, size, false
            );

            // Dessiner l’étoile
            canvas.drawBitmap(resizedStar, startX, starY, null);

            // Mise à jour de la position X pour la prochaine étoile
            startX += size + spacing;
        }
    }
    // Appeler cette méthode quand le joueur réussit des tuiles
    private void showFeedback(String message) {
        feedbackText = message;
        feedbackStartTime = System.currentTimeMillis();
    }

    // Dessiner le feedback animé
    private void drawFeedback(Canvas canvas) {
        if (!feedbackText.isEmpty()) {
            long elapsedTime = System.currentTimeMillis() - feedbackStartTime;

            if (elapsedTime < FEEDBACK_DURATION) {
                // Gestion de l’opacité pour le fade-out progressif
                int alpha = 255;
                if (elapsedTime > FEEDBACK_DURATION / 2) {
                    alpha = (int) ((1 - ((elapsedTime - FEEDBACK_DURATION / 2) / (FEEDBACK_DURATION / 2.0))) * 255);
                }

                float x = screenWidth / 2;
                float y = screenHeight / 3;

                // Effet néon : plusieurs couches de texte flou
                Paint neonPaint = new Paint();
                neonPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                neonPaint.setTextSize(120);
                neonPaint.setColor(Color.WHITE);
                neonPaint.setTextAlign(Paint.Align.CENTER);
                neonPaint.setAlpha(alpha);
                neonPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER));

                for (int i = 5; i > 0; i--) {  // Ajoute plusieurs couches pour un effet plus intense
                    neonPaint.setAlpha(alpha / (i + 1)); // Atténuation progressive du halo
                    canvas.drawText(feedbackText, x, y, neonPaint);
                }

                // Texte principal en blanc pur
                Paint mainPaint = new Paint();
                mainPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                mainPaint.setTextSize(120);
                mainPaint.setColor(Color.WHITE);
                mainPaint.setTextAlign(Paint.Align.CENTER);
                mainPaint.setAlpha(alpha);

                canvas.drawText(feedbackText, x, y, mainPaint);
            } else {
                feedbackText = ""; // Cacher le feedback après expiration
            }
        }
    }



   /* private void drawDynamicText(Canvas canvas) {
        String message = "";
        if (score < 10) {
            message = "COOL";
        } else if (score < 20) {
            message = "PARFAIT";
        } else {
            message = "AMAZING";
        }

        paint.setTextSize(80);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        float messageX = screenWidth / 2;
        float messageY = 400; // En dessous du score

        canvas.drawText(message, messageX, messageY, paint);
    }*/




    private void showGameOverDialog() {
        // Vérifier si le contexte est bien une instance d'Activity
        if (getContext() instanceof Activity) {
            // Cast du Context en Activity
            Activity activity = (Activity) getContext();

            // Passer l'Activity au GameOverDialog
            new GameOverDialog(activity).show(score);
        }
    }



    private void showPauseMenu() {
        // Vérification si le Context est une instance d'Activity
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.pause_menu, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);

                // Supprimer le fond gris de la boîte de dialogue
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

                Button resumeButton = dialogView.findViewById(R.id.resume_button);
                resumeButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    isPlaying = true;
                    startGame();
                });

                Button restartButton = dialogView.findViewById(R.id.restart_button);
                restartButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    restartGame();
                });

                Button homeButton = dialogView.findViewById(R.id.home_button);
                homeButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    getContext().startActivity(intent);
                    activity.finish();
                });
                Button settingsButton = dialogView.findViewById(R.id.settings_button);
                settingsButton.setOnClickListener(v -> {
                    Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                    getContext().startActivity(settingsIntent);
                });

                dialog.show();
            });
        }
    }



    public void restartGame() {
        stopGame();  // Arrêter le jeu en cours
        resetGameState(); // Réinitialiser toutes les variables
        startGame(); // Démarrer une nouvelle partie
    }


    public void resetGameState() {
        tiles.clear();  // Supprime toutes les tuiles
        score = 0;  // Réinitialise le score
        lastMissedTile = null; // Réinitialise la dernière tuile ratée
        lastSpeedIncreaseTime = System.currentTimeMillis(); // Remettre le temps à zéro
        tileSpeed = 15; // Réinitialiser la vitesse
        invalidate(); // Redessine l’écran proprement
    }



    public void startGame() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
        soundManager.playBackgroundMusic();
    }

    public void stopGame() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundManager.stopBackgroundMusic();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > screenWidth - pauseButtonImage.getWidth() - 20 &&
                    event.getX() < screenWidth - 20 &&
                    event.getY() > 20 && event.getY() < 20 + pauseButtonImage.getHeight()) {
                pauseGame();
            }

            Iterator<Tile> iterator = tiles.iterator();
            while (iterator.hasNext()) {
                Tile tile = iterator.next();
                if (event.getX() > tile.x && event.getX() < tile.x + tile.width &&
                        event.getY() > tile.y && event.getY() < tile.y + tile.height) {
                    iterator.remove();
                    soundManager.playSound("piano_note1");
                    score++;
                    if (score == 5) {
                        showFeedback("COOL!");
                    } else if (score == 10) {
                        showFeedback("PARFAIT!");
                    } else if (score == 20) {
                        showFeedback("AMAZING!");
                    }



                    break;
                }
            }
        }
        return true;
    }

    private void pauseGame() {
        isPlaying = false;
        showPauseMenu();
    }
}