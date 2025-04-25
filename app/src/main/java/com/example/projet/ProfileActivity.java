package com.example.projet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ProfileActivity extends BaseActivity {

    private TextView usernameTextView, displayNameTextView;
    private ImageView editIcon;
    private Button logoutButton;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialisation des vues
        usernameTextView = findViewById(R.id.usernameTextView);
        ImageView avatarImageView = findViewById(R.id.avatarImageView);
        editIcon = findViewById(R.id.editIcon);
        logoutButton = findViewById(R.id.logoutButton);
        TextView bioTextView = findViewById(R.id.bioTextView);

        // Chargement des données du profil
        loadProfileData();

        // Lancer EditProfileActivity
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        });

        // Fermer l'activité
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());

        // Déconnexion avec popup personnalisé
        logoutButton.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
            ImageView sadGif = dialogView.findViewById(R.id.sadGif);
            TextView confirmationText = dialogView.findViewById(R.id.logout_confirmation_text);
            Button yesButton = dialogView.findViewById(R.id.yes_button);
            Button noButton = dialogView.findViewById(R.id.no_button);

            // Texte de confirmation visible
            confirmationText.setText("Are you sure you want to log out?");
            confirmationText.setTextColor(getResources().getColor(android.R.color.white)); // Assure qu'il est blanc

            // Charger le GIF
            Glide.with(ProfileActivity.this)
                    .asGif()
                    .load(R.drawable.sad)
                    .into(sadGif);

            AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                    .setView(dialogView)
                    .create();

            // Bouton "Oui" avec animation
            yesButton.setOnClickListener(view -> {
                animateButtonClick(view);
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                prefs.edit()
                        .remove("loggedInUsername")
                        .remove("displayName")
                        .remove("avatarId")
                        .remove("bio")
                        .apply();

                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            });

            noButton.setOnClickListener(view -> {
                animateButtonClick(view);
                // Délai pour laisser l'animation se jouer avant de fermer
                view.postDelayed(dialog::dismiss, 200);
            });


            // Affichage + fond transparent
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });
    }

    // ➕ Animation clic pour effet visuel
    private void animateButtonClick(View button) {
        button.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                }).start();
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("loggedInUsername", "");
        String displayName = prefs.getString("displayName", "Nom d'affichage");
        int avatarId = prefs.getInt("avatarId", R.drawable.a1);

        usernameTextView.setText(currentUsername);

        ((ImageView) findViewById(R.id.avatarImageView)).setImageResource(avatarId);
        String bio = prefs.getString("bio", "");
        ((TextView) findViewById(R.id.bioTextView)).setText(bio);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            loadProfileData();
        }
    }
}
