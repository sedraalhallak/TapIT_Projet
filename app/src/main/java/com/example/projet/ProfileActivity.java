package com.example.projet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        displayNameTextView = findViewById(R.id.displayNameTextView);
        ImageView avatarImageView = findViewById(R.id.avatarImageView);
        editIcon = findViewById(R.id.editIcon);
        logoutButton = findViewById(R.id.logoutButton);

        // Récupérer les informations depuis SharedPreferences
        loadProfileData();


        // Gestion du clic sur l'icône de modification
        // Modifiez le onClickListener de editIcon :
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivityForResult(intent, 1); // Utilisez startActivityForResult au lieu de startActivity
        });

        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Retour à l'activité précédente
            finish();
        });


        // Gestion du clic sur le bouton de déconnexion
        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit()
                    .remove("loggedInUsername")
                    .remove("displayName")
                    .remove("avatarId")
                    .apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


    }
    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("loggedInUsername", "");
        String displayName = prefs.getString("displayName", "Nom d'affichage");
        int avatarId = prefs.getInt("avatarId", R.drawable.a1);

        usernameTextView.setText(currentUsername);
        displayNameTextView.setText(displayName);
        ((ImageView)findViewById(R.id.avatarImageView)).setImageResource(avatarId);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            loadProfileData();

        }
    }



}