package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationHelper {

    public static void setupNavigationBar(Activity activity) {
        Animation clickAnimation = AnimationUtils.loadAnimation(activity, R.anim.click_scale);

        LinearLayout homeButton = activity.findViewById(R.id.homeButton);
        LinearLayout musicButton = activity.findViewById(R.id.musicButton);
        LinearLayout favoriteButton = activity.findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = activity.findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            if (!(activity instanceof HomeActivity)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Home", Toast.LENGTH_SHORT).show();
            }
        });

        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            if (!(activity instanceof SelectionActivity)) {
                Intent intent = new Intent(activity, SelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Music", Toast.LENGTH_SHORT).show();
            }
        });

        favoriteButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            if (!(activity instanceof FavoritesActivity)) {
                Intent intent = new Intent(activity, FavoritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Favorites", Toast.LENGTH_SHORT).show();
            }
        });


        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            if (!(activity instanceof SettingsActivity)) {
                Intent intent = new Intent(activity, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
