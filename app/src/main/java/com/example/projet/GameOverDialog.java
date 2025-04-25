package com.example.projet;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameOverDialog extends BaseActivity {
    private AlertDialog dialog;
    private final Context context;
    private final String songTitle;

    public GameOverDialog(Context context, String songTitle) {
        this.context = context;
        this.songTitle = songTitle;
    }

    public void show(int score) {
        Activity activity = (Activity) context;
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TransparentDialog);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.game_over_dialog, null);
            builder.setView(dialogView);

            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView scoreText = dialogView.findViewById(R.id.score_text);
            scoreText.setText("Score : " + score);

            saveStars(score);


            Button restartButton = dialogView.findViewById(R.id.continue_button);
            restartButton.setText(R.string.restart);
            applyClickEffect(restartButton);
            restartButton.setOnClickListener(v -> {

                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.restartMusic();
                    if (mainActivity.getGameView() != null) {
                        mainActivity.getGameView().restartGame();
                    }
                }
                dialog.dismiss();
            });

            Button homeButton = dialogView.findViewById(R.id.home_button);
            applyClickEffect(homeButton);
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                activity.finish();

            });

            dialog.show();
        });
    }

    private void saveStars(int score) {
        int starsEarned = Math.min(score / 2, 3);

        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUsername", null);
        if (username != null) {
            SharedPreferences starPrefs = context.getSharedPreferences("SongStars", MODE_PRIVATE);
            String key = username + "_" + songTitle;
            starPrefs.edit().putInt(key, starsEarned).apply();
        }

    }

    private void applyClickEffect(Button button) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        button,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                );
                scaleDown.setDuration(100);
                scaleDown.start();

                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(50);
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        button,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                );
                scaleUp.setDuration(100);
                scaleUp.start();
            }
            return false;
        });
    }
}


