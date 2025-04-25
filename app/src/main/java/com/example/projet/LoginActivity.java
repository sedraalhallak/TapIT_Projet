package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        EditText usernameInput = findViewById(R.id.username); // Champ nom d'utilisateur
        EditText passwordInput = findViewById(R.id.password); // Champ mot de passe

        // Bouton de connexion
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                prefs.edit().putString("loggedInUsername", username).apply();

            } else {
                Toast.makeText(LoginActivity.this, "Nom d'utilisateur ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
            }
        });

        // Redirection vers la page d'inscription
        findViewById(R.id.go_to_signup).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Fermer l'activité au clic sur l'icône de fermeture (X)
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish()); // Ferme l'activité actuelle
    }
}