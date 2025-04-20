package com.example.projet;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;

public class ProfileUtils {
    public static void setupProfileAvatar(Activity activity, int avatarViewId) {
        ImageView profileAvatar = activity.findViewById(avatarViewId);
        SharedPreferences prefs = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Charger l'avatar
        int avatarId = prefs.getInt("avatarId", R.drawable.a1);
        profileAvatar.setImageResource(avatarId);

        // Gestion du clic
        profileAvatar.setOnClickListener(v -> {
            String loggedInUser = prefs.getString("loggedInUsername", null);
            Intent intent = new Intent(activity,
                    loggedInUser != null ? ProfileActivity.class : LoginActivity.class);
            activity.startActivity(intent);
        });
    }
}