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
    private boolean isPlaying = true;
    private boolean isGameOver = false;
    private boolean isDialogShown = false;

    private boolean isPaused = false;

    private SurfaceHolder holder;
    private Paint paint;
    private ArrayList<Tile> tiles;
    private int screenWidth, screenHeight;
    private int tileWidth, tileHeight;
    private int tileSpeed ;
    private int score = 0;
    private Random random;
    private SoundManager soundManager;





    private Bitmap background;
    private Bitmap tileImage;
    private Bitmap pauseButtonImage;
    private Bitmap redTileImage;  // Image pour la tuile rouge
    private Tile lastMissedTile = null; // Dernière tuile ratée

    private long lastSpeedIncreaseTime = 0;

    private final int SPEED_INCREASE_INTERVAL = 3000;
    ; // Augmenter la hauteur pour que ce soit plus rectangulaire
    private Bitmap star;
    private Bitmap filledStar;
    // Variables globales pour le feedback
    private String feedbackText = "";
    private long feedbackStartTime = 0;
    private final int FEEDBACK_DURATION = 1000; // Durée en ms (1 seconde)
    private int[] lastTileY;
    public GameView(Context context, int width, int height) {
        super(context);
        screenWidth = width;
        screenHeight = height;
        holder = getHolder();
        paint = new Paint();
        tiles = new ArrayList<>();
        random = new Random();
        soundManager = new SoundManager(context);

        tileWidth = screenWidth / 4;
        tileHeight = 300;
        tileSpeed = 25;
        //lastTileY = new int[4];

        background = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);

        tileImage = BitmapFactory.decodeResource(getResources(), R.drawable.tile_button);
        tileImage = Bitmap.createScaledBitmap(tileImage, tileWidth, tileHeight, false);

        pauseButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        pauseButtonImage = Bitmap.createScaledBitmap(pauseButtonImage, 100, 100, false);

        redTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        redTileImage = Bitmap.createScaledBitmap(redTileImage,(int)(screenWidth / 3.2), 450, false);

        soundManager.playBackgroundMusic();
        // Chargement et redimensionnement des étoiles UNE SEULE FOIS
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        star = Bitmap.createScaledBitmap(star, 100, 100, false);

        filledStar = BitmapFactory.decodeResource(getResources(), R.drawable.filled_star);
        filledStar = Bitmap.createScaledBitmap(filledStar, 100, 100, false);
        addTile();
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
        int tileX = column * tileWidth; // Placer la tuile au début de la colonne
        // Ajouter la tuile avec sa position X et Y
        tiles.add(new Tile(tileX, -tileHeight, tileWidth, tileHeight));
    }


    private void update() {

        if (isGameOver) return;
        tileSpeed = 25 + (score / 5);

        Iterator<Tile> iterator = tiles.iterator();
        while (iterator.hasNext()) {
            Tile tile = iterator.next();
            tile.y += tileSpeed;

            if (tile.y + tile.height > screenHeight && !tile.isError) {
                tile.isError = true;
                isGameOver = true;
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

            paint.setColor(Color.LTGRAY);
            for (int i = 1; i < 4; i++) {
                canvas.drawLine(i * tileWidth, 0, i * tileWidth, screenHeight, paint);
            }

            for (Tile tile : tiles) {

                //Bitmap tileBitmap = (tile.isError) ? redTileImage : tileImage;
                //canvas.drawBitmap(tileBitmap, tile.x, tile.y, null);
                paint.setColor(tile.isError ? Color.RED : Color.BLACK);
                canvas.drawRect(tile.x, tile.y, tile.x + tile.width, tile.y + tile.height, paint);
            }
            if (isGameOver && !isDialogShown) {
                isDialogShown = true;
                Activity activity = (Activity) getContext();
                activity.runOnUiThread(() -> {
                    new GameOverDialog(activity).show(score);
                });
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




    private void showPauseMenu() {
        // Vérification si le Context est une instance d'Activity
        isGameOver=false;
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
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        dialog.dismiss();
                        isPaused = false;
                        isPlaying = true;
                        startGame();
                    });
                });

                Button restartButton = dialogView.findViewById(R.id.restart_button);
                restartButton.setOnClickListener(v -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        dialog.dismiss();
                        isPaused = false;
                        isPlaying = true;
                        restartGame();
                    });
                });

                Button homeButton = dialogView.findViewById(R.id.home_button);
                homeButton.setOnClickListener(v -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        getContext().startActivity(intent);
                        activity.finish();
                    });
                });
                Button settingsButton = dialogView.findViewById(R.id.settings_button);
                settingsButton.setOnClickListener(v -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                        getContext().startActivity(settingsIntent);
                    });
                });

                dialog.show();
            });
        }
    }



    public void restartGame() {
        resetGameState(); // Réinitialiser toutes les variables
        startGame(); // Démarrer une nouvelle parti

    }


    public void resetGameState() {
        tiles.clear();  // Supprime toutes les tuiles
        score = 0;  // Réinitialise le score
        tileSpeed = 25; // Réinitialiser la vitesse
        isDialogShown = false;
        isGameOver = false;
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

    private void pauseGame() {
        isPlaying = false;
        isPaused = true;
        showPauseMenu();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver || isPaused) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Vérifier si le bouton pause est touché
            if (touchX > screenWidth - pauseButtonImage.getWidth() - 20 &&
                    touchX < screenWidth - 20 &&
                    touchY > 20 && touchY < 20 + pauseButtonImage.getHeight()) {
                pauseGame();
                return true;
            }

            // Vérifier si le clic est DANS la zone des colonnes
            if (touchX < 0 || touchX > screenWidth) {
                return false; // clic hors de la grille → on ignore
            }

            int column = (int)(touchX / tileWidth); // déterminer la colonne cliquée

            // Chercher la tuile noire dans cette colonne et à cette hauteur
            Tile clickedTile = null;
            for (Tile tile : tiles) {
                boolean isInSameColumn = tile.x == column * tileWidth;
                boolean isWithinHeight = touchY >= tile.y && touchY <= tile.y + tile.height;
                if (!tile.isError && isInSameColumn && isWithinHeight) {
                    clickedTile = tile;
                    break;
                }
            }

            if (clickedTile != null) {
                // Tuile touchée correctement
                tiles.remove(clickedTile);
                score++;
                showFeedback("Great!");
            } else {
                // Clic dans la colonne mais pas sur une tuile noire → Miss Click
                // Ajoute un faux "miss click" dans la colonne, mais seulement si le clic est dans la grille
                boolean isInTileArea = false;
                for (Tile tile : tiles) {
                    if (tile.x == column * tileWidth && touchY >= tile.y && touchY <= tile.y + tile.height) {
                        isInTileArea = true;
                        break;
                    }
                }

                if (!isInTileArea) {
                    // Un clic dans la zone d'une colonne sans toucher une tuile valide
                    isGameOver = true; // On met fin au jeu
                    // Optionnel : Ajouter un retour visuel comme une tuile d'erreur fictive
                    Tile fakeError = new Tile(column * tileWidth, (int)touchY, tileWidth, tileHeight);
                    fakeError.isError = true;
                    tiles.add(fakeError);
                }
            }

            return true;
        }

        return false;
    }




}