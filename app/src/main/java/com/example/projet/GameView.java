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
    private Bitmap redTileImage;
    private Bitmap star;
    private Bitmap filledStar;
    private int baseSpeed = 25;
    private int speedIncreaseInterval = 5000;
    private long lastSpeedIncreaseTime = System.currentTimeMillis();
    private float speedMultiplier = 1.0f;

    private String feedbackText = "";
    private long feedbackStartTime = 0;
    private final int FEEDBACK_DURATION = 1000;

    // Variables pour l'effet de flash
    private int flashAlpha = 0;

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

        // load ressources
        background = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        pauseButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.pause_button);
        pauseButtonImage = Bitmap.createScaledBitmap(pauseButtonImage, 100, 100, false);
        redTileImage = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        redTileImage = Bitmap.createScaledBitmap(redTileImage, (int) (screenWidth / 3.2), 450, false);

        // load star dimentions
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star1);
        star = Bitmap.createScaledBitmap(star, 80, 80, false);
        filledStar = BitmapFactory.decodeResource(getResources(), R.drawable.filled_star);
        filledStar = Bitmap.createScaledBitmap(filledStar, 80, 80, false);

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
                    new GameOverDialog(activity, ((MainActivity) activity).getSongTitle()).show(score);
                });
            }
            canvas.drawBitmap(pauseButtonImage, screenWidth - pauseButtonImage.getWidth() - 20, 20, null);
            drawStars(canvas);
            drawScore(canvas);
            drawFeedback(canvas);
            drawFlash(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawScore(Canvas canvas) {
        float scoreX = screenWidth / 2;
        float scoreY = 450;
        Paint mainPaint = new Paint();
        mainPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mainPaint.setTextSize(120);
        mainPaint.setColor(Color.WHITE);
        mainPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + score, scoreX, scoreY, mainPaint);
    }

    private void drawStars(Canvas canvas) {
        int maxStars = 3;
        int filledStars = Math.min(score / 2, maxStars);
        int[] starSizes = {200, 200, 200};
        int spacing = 30;
        int totalWidth = starSizes[0] + starSizes[1] + starSizes[2] + (2 * spacing);
        int startX = (screenWidth / 2) - (totalWidth / 2);
        int starY = 150;


        long currentTime = System.currentTimeMillis();
        float opacity = (float) Math.abs(Math.sin(currentTime / 500.0)); // Flickering effect
        for (int i = 0; i < maxStars; i++) {
            int size = starSizes[i];
            boolean isFilled = i < filledStars;
            drawStarWithOpacity(canvas, startX + size / 2, starY + size / 2, size / 2, isFilled, opacity);
            startX += size + spacing;
        }
    }

    private void drawStarWithOpacity(Canvas canvas, float centerX, float centerY, float radius, boolean filled, float opacity) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(filled ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        if (filled) {
            paint.setColor(Color.parseColor("#FFD700"));
            paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
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
                        MainActivity mainActivity = (MainActivity) getContext();
                        String songTitle = mainActivity.getSongTitle();
                        int currentScore = score;
                        if (currentScore > mainActivity.getSongHighScore()) {
                            mainActivity.updateSongHighScore(songTitle, currentScore);
                        }
                    }
                    if (getContext() instanceof MainActivity) {
                        ((MainActivity) getContext()).stopMusic();
                    }
                    if (!isDialogShown) {
                        isDialogShown = true;
                        Activity activity = (Activity) getContext();
                        activity.runOnUiThread(() -> {
                            new GameOverDialog(activity, ((MainActivity) activity).getSongTitle()).show(score);
                        });
                    }
                }
            }
        }
        return true;
    }
}