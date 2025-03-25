package com.example.projet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverDialog {
    private AlertDialog dialog;
    private Context context;

    public GameOverDialog(Context context) {
        this.context = context;
    }

    public void show(int score) {
        Activity activity = (Activity) context;
        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.game_over_dialog, null);
            builder.setView(dialogView);

            dialog = builder.create();
            dialog.setCancelable(false);

            TextView scoreText = dialogView.findViewById(R.id.score_text);
            scoreText.setText("Score : " + score);

            Button continueButton = dialogView.findViewById(R.id.continue_button);
            continueButton.setOnClickListener(v -> {
                dialog.dismiss();
                ((GameView) ((Activity) context).findViewById(R.id.gameView)).restartGame();
            });

            Button homeButton = dialogView.findViewById(R.id.home_button);
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, HomeActivity.class); // Assurez-vous que c'est bien l'accueil
                context.startActivity(intent);
                activity.finish();
            });

            dialog.show();
        });
    }
}
