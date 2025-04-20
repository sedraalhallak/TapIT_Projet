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
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
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
import java.util.List;
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
    private int tileSpeed;
    private int score = 0;
    private Random random;
    private Bitmap background;
    private Bitmap tileImage;
    private Bitmap pauseButtonImage;
    private Bitmap redTileImage;  // Image pour la tuile rouge
    private Bitmap star;
    private Bitmap filledStar;
    private ArrayList<Particle> festiveParticles = new ArrayList<>();
    private boolean hasCelebrated = false;
    private final int SPEED_INCREASE_INTERVAL = 3000;
    private int baseSpeed = 25; // Vitesse de base
    private int speedIncreaseInterval = 5000; // Intervalle d'augmentation de vitesse (5 secondes)
    private long lastSpeedIncreaseTime = System.currentTimeMillis();
    private float speedMultiplier = 1.0f; // Multiplicateur de vitesse initial

    // Variables globales pour le feedback
    private String feedbackText = "";
    private long feedbackStartTime = 0;
    private final int FEEDBACK_DURATION = 1000; // Durée en ms (1 seconde)

    // Variables pour l'effet de flash
    private int flashAlpha = 0;
    int starY = 150;

    public GameView(Context context, int width, int height) {
        super(context);
        screenWidth = width;
        screenHeight = height;
        holder = getHolder();
        paint = new Paint();
        tiles = new ArrayList<>();
        random = new Random();
        tileWidth = screenWidth / 4;
        tileHeight = 300;
        tileSpeed = 25;

        // Chargement des ressources
        background = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        pauseButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        pauseButtonImage = Bitmap.createScaledBitmap(pauseButtonImage, 100, 100, false);
        redTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        redTileImage = Bitmap.createScaledBitmap(redTileImage, (int) (screenWidth / 3.2), 450, false);

        // Chargement et redimensionnement des étoiles
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star1);
        star = Bitmap.createScaledBitmap(star, 80, 80, false);
        filledStar = BitmapFactory.decodeResource(getResources(), R.drawable.filled_star);
        filledStar = Bitmap.createScaledBitmap(filledStar, 80, 80, false);

        addTile();
    }

    private void generateFestiveParticles() {
        int centerX = screenWidth / 2;
        int centerY = starY + 100;

        // Ajouter un grand nombre de particules pour l'effet explosif
        for (int i = 0; i < 100; i++) { // Augmenter le nombre de particules pour un effet plus spectaculaire
            festiveParticles.add(new Particle(centerX, centerY, true));
        }
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
        if (tiles.isEmpty()) {
            spawnTileInRandomColumn();
            spawnTileInRandomColumn();
            return;
        }
        Tile lastTile = tiles.get(tiles.size() - 1);
        if (lastTile.y > tileHeight / 2) {
            spawnTileInRandomColumn();
        }
    }

    private void spawnTileInRandomColumn() {
        int column;
        do {
            column = random.nextInt(4);
        } while (!isColumnValid(column));
        int minGap = tileHeight;
        int spawnY = -tileHeight;
        if (!tiles.isEmpty()) {
            Tile lastTile = tiles.get(tiles.size() - 1);
            spawnY = lastTile.y - minGap;
        }
        tiles.add(new Tile(column * tileWidth, spawnY, tileWidth, tileHeight));
    }

    private boolean isColumnValid(int newColumn) {
        if (tiles.isEmpty()) return true;
        Tile lastTile = tiles.get(tiles.size() - 1);
        int lastColumn = lastTile.x / tileWidth;
        return newColumn != lastColumn;
    }

    private void update() {
        if (isGameOver) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpeedIncreaseTime > speedIncreaseInterval) {
            speedMultiplier += 0.1f;
            lastSpeedIncreaseTime = currentTime;
        }
        tileSpeed = (int) (baseSpeed * speedMultiplier);
        tileSpeed += score / 5;
        for (Tile tile : tiles) {
            tile.y += tileSpeed;
            if (tile.y + tile.height > screenHeight && !tile.isError) {
                tile.isError = true;
                isGameOver = true;
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).stopMusic();
                }
            }
        }
        if (shouldAddTile()) {
            addTile();
        }
        // Met à jour les particules festives
        Iterator<Particle> iterator = festiveParticles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (!p.isAlive()) {
                iterator.remove();
            }
        }

    }

    private boolean shouldAddTile() {
        return tiles.isEmpty() || tiles.get(tiles.size() - 1).y > 100;
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawBitmap(background, 0, 0, null);
            paint.setColor(Color.LTGRAY);
            for (int i = 1; i < 4; i++) {
                canvas.drawLine(i * tileWidth, 0, i * tileWidth, screenHeight, paint);
            }
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5);
            for (int i = 1; i < 4; i++) {
                canvas.drawLine(i * tileWidth, 0, i * tileWidth, screenHeight, paint);
            }
            for (Tile tile : tiles) {
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
            canvas.drawBitmap(pauseButtonImage, screenWidth - pauseButtonImage.getWidth() - 20, 20, null);
            drawStars(canvas);
            drawScore(canvas);
            drawFeedback(canvas);
            drawFlash(canvas);
            for (Particle p : festiveParticles) {
                p.draw(canvas);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawScore(Canvas canvas) {
        float scoreX = screenWidth / 2;
        float scoreY = 450; // Position du score

        Paint mainPaint = new Paint();
        mainPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mainPaint.setTextSize(120);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("" + score, scoreX, scoreY, mainPaint);
    }

    private void drawStars(Canvas canvas) {
        int maxStars = 3;
        int filledStars = Math.min(score / 2, maxStars); // Maximum 3 stars

        int[] starSizes = {200, 200, 200}; // Custom size for each star
        int spacing = 30;

        int totalWidth = starSizes[0] + starSizes[1] + starSizes[2] + (2 * spacing);
        int startX = (screenWidth / 2) - (totalWidth / 2);
        int starY = 150;

        // Animation effect for star flickering
        long currentTime = System.currentTimeMillis();
        float opacity = (float) Math.abs(Math.sin(currentTime / 500.0)); // Flickering effect

        for (int i = 0; i < maxStars; i++) {
            int size = starSizes[i];
            boolean isFilled = i < filledStars;

            // Make the filled star flicker by adjusting its opacity
            drawStarWithOpacity(canvas, startX + size / 2, starY + size / 2, size / 2, isFilled, opacity);
            startX += size + spacing;
        }

        // 🎉 Trigger the light burst when 3 stars are filled
        if (filledStars == 3 && !hasCelebrated) {
            generateLightBurst(); // Generate the festive particles
            hasCelebrated = true; // Ensure it only happens once
        }
    }

    private void generateLightBurst() {
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        for (int i = 0; i < 250; i++) {
            festiveParticles.add(new Particle(centerX, centerY, true));
        }

        triggerFlash();


        //soundManager.playSoundEffect(SoundManager.EFFECT_SPARKLE);
    }


    // Méthode pour créer une étoile à 5 branches
    private Path createStarPath(float centerX, float centerY, float radius) {
        Path path = new Path();
        int numPoints = 5; // Nombre de branches
        double angle = Math.PI / numPoints; // Angle entre chaque point
        for (int i = 0; i < 2 * numPoints; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.5; // Alterner entre rayon externe et interne
            double a = i * angle - Math.PI / 2; // Décalage pour orienter l'étoile vers le haut
            float x = (float) (centerX + r * Math.cos(a));
            float y = (float) (centerY + r * Math.sin(a));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close(); // Fermer le chemin pour compléter l'étoile
        return path;
    }

    private void drawStarWithOpacity(Canvas canvas, float centerX, float centerY, float radius, boolean filled, float opacity) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(filled ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        if (filled) {
            paint.setColor(Color.parseColor("#FFD700"));  // Couleur or
            paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));  // Effet lumineux
        } else {
            paint.setColor(Color.LTGRAY);
        }

        paint.setAlpha((int) (255 * opacity));

        Path path = new Path();
        int numPoints = 5;
        double angle = Math.PI / numPoints;

        for (int i = 0; i < 2 * numPoints; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.5;
            double a = i * angle - Math.PI / 2;
            float x = (float) (centerX + r * Math.cos(a));
            float y = (float) (centerY + r * Math.sin(a));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();

        canvas.drawPath(path, paint);
    }


    private void drawLightRays(Canvas canvas) {
        Paint rayPaint = new Paint();
        rayPaint.setColor(Color.YELLOW);
        rayPaint.setAlpha(40);
        rayPaint.setStyle(Paint.Style.FILL);
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        for (int i = 0; i < 12; i++) {
            float angle = (float) (i * Math.PI / 6);
            float length = 1000;
            float endX = (float) (centerX + length * Math.cos(angle));
            float endY = (float) (centerY + length * Math.sin(angle));
            Path ray = new Path();
            ray.moveTo(centerX, centerY);
            ray.lineTo(endX + 30, endY + 30);
            ray.lineTo(endX - 30, endY - 30);
            ray.close();
            canvas.drawPath(ray, rayPaint);
        }
    }

    private void triggerFlash() {
        flashAlpha = 255;
    }

    private void drawFlash(Canvas canvas) {
        if (flashAlpha > 0) {
            Paint flashPaint = new Paint();
            flashPaint.setColor(Color.WHITE);
            flashPaint.setAlpha(flashAlpha);
            canvas.drawRect(0, 0, screenWidth, screenHeight, flashPaint);
            flashAlpha -= 10;
        }
    }

    private void showFeedback(String message) {
        feedbackText = message;
        feedbackStartTime = System.currentTimeMillis();
    }

    private void drawFeedback(Canvas canvas) {
        if (!feedbackText.isEmpty()) {
            long elapsedTime = System.currentTimeMillis() - feedbackStartTime;
            if (elapsedTime < FEEDBACK_DURATION) {
                int alpha = 255;
                if (elapsedTime > FEEDBACK_DURATION / 2) {
                    alpha = (int) ((1 - ((elapsedTime - FEEDBACK_DURATION / 2) / (FEEDBACK_DURATION / 2.0))) * 255);
                }
                float x = screenWidth / 2;
                float y = screenHeight / 3;
                Paint neonPaint = new Paint();
                neonPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                neonPaint.setTextSize(120);
                neonPaint.setColor(Color.WHITE);
                neonPaint.setTextAlign(Paint.Align.CENTER);
                neonPaint.setAlpha(alpha);
                neonPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER));
                for (int i = 5; i > 0; i--) {
                    neonPaint.setAlpha(alpha / (i + 1));
                    canvas.drawText(feedbackText, x, y, neonPaint);
                }
                Paint mainPaint = new Paint();
                mainPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                mainPaint.setTextSize(120);
                mainPaint.setColor(Color.WHITE);
                mainPaint.setTextAlign(Paint.Align.CENTER);
                mainPaint.setAlpha(alpha);
                canvas.drawText(feedbackText, x, y, mainPaint);
            } else {
                feedbackText = "";
            }
        }
    }

    private void showPauseMenu() {
        isGameOver = false;
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.pause_menu, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                Button resumeButton = dialogView.findViewById(R.id.resume_button);
                resumeButton.setOnClickListener(v -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        dialog.dismiss();
                        isPaused = false;
                        isPlaying = true;
                        ((MainActivity) getContext()).resumeMusic();
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
                        ((MainActivity) getContext()).restartMusic();
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
        resetGameState();
        startGame();
    }

    public void resetGameState() {
        tiles.clear();
        score = 0;
        speedMultiplier = 1.0f;
        lastSpeedIncreaseTime = System.currentTimeMillis();
        isDialogShown = false;
        isGameOver = false;
    }

    public void startGame() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void pauseGame() {
        isPlaying = false;
        isPaused = true;
        ((MainActivity) getContext()).pauseMusic();
        showPauseMenu();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver || isPaused) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();
            if (touchX > screenWidth - pauseButtonImage.getWidth() - 20 &&
                    touchX < screenWidth - 20 &&
                    touchY > 20 && touchY < 20 + pauseButtonImage.getHeight()) {
                pauseGame();
                return true;
            }
            boolean hitValidTile = false;
            int column = (int) (touchX / tileWidth);
            float clickY = touchY;
            List<Tile> tilesCopy = new ArrayList<>(tiles);
            for (Tile tile : tilesCopy) {
                if (tile.x == column * tileWidth &&
                        clickY >= tile.y && clickY <= tile.y + tile.height) {
                    tiles.remove(tile);
                    score++;
                    showFeedback("Great!");
                    hitValidTile = true;
                    break;
                }
            }
            if (!hitValidTile) {
                boolean canPlaceRedTile = true;
                for (Tile tile : tiles) {
                    if (tile.x == column * tileWidth &&
                            Math.abs(tile.y - clickY) < tileHeight) {
                        canPlaceRedTile = false;
                        break;
                    }
                }
                if (canPlaceRedTile) {
                    Tile errorTile = new Tile(column * tileWidth, (int) clickY, tileWidth, tileHeight);
                    errorTile.isError = true;
                    tiles.add(errorTile);
                    isGameOver = true;
                    if (getContext() instanceof MainActivity) {
                        ((MainActivity) getContext()).stopMusic();
                    }
                    if (!isDialogShown) {
                        isDialogShown = true;
                        Activity activity = (Activity) getContext();
                        activity.runOnUiThread(() -> {
                            new GameOverDialog(activity).show(score);
                        });
                    }
                }
            }
        }
        return true;
    }

    // Classe Particle pour les effets de particules
    class Particle {
        private float x, y;
        private float dx, dy;
        private float radius;
        private Paint paint;
        private int alpha;
        private boolean isGolden;

        public Particle(float x, float y, boolean isGolden) {
            this.x = x;
            this.y = y;
            this.isGolden = isGolden;
            Random random = new Random();

            // 💨 Mouvement plus rapide
            this.dx = (random.nextFloat() - 0.5f) * 30;
            this.dy = (random.nextFloat() - 0.5f) * 30;

            // ✨ Taille plus grande
            this.radius = random.nextFloat() * 8f + 4f;

            // 🟡 Couleur dorée
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(isGolden ? Color.rgb(255, 215, 0) : Color.WHITE);
            paint.setStyle(Paint.Style.FILL);

            // 🌫 Transparence initiale
            this.alpha = 255;
        }

        public void update() {
            x += dx;
            y += dy;
            alpha -= 4; // Durée de vie réduite
            if (alpha < 0) alpha = 0;
        }

        public void draw(Canvas canvas) {
            paint.setAlpha(alpha);

            if (isGolden) {
                // 🌟 Glow doré
                RadialGradient gradient = new RadialGradient(
                        x, y, radius * 2,
                        Color.argb(alpha, 255, 223, 0),
                        Color.argb(0, 255, 223, 0),
                        Shader.TileMode.CLAMP
                );
                paint.setShader(gradient);
                canvas.drawCircle(x, y, radius * 2, paint);
                paint.setShader(null);
            }

            // 💫 Coeur de la particule
            canvas.drawCircle(x, y, radius, paint);
        }

        public boolean isAlive() {
            return alpha > 0;
        }
    }
}
