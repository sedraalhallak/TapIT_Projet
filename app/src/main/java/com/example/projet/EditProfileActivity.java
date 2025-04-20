package com.example.projet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class EditProfileActivity extends BaseActivity {

    private EditText editUsername, editDisplayName;
    private Button saveButton;
    private ImageView editAvatarImageView;
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private int selectedAvatarId = R.drawable.a1; // ID par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialisation des vues
        editAvatarImageView = findViewById(R.id.editAvatarImageView);
        editUsername = findViewById(R.id.editUsername);
        editDisplayName = findViewById(R.id.editDisplayName);
        saveButton = findViewById(R.id.saveButton);

        // Récupérer l'utilisateur connecté
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("loggedInUsername", "");

        // Charger les valeurs actuelles
        editUsername.setText(currentUsername);
        editDisplayName.setText(prefs.getString("displayName", "Nom d'affichage"));

        // Charger l'avatar actuel
        selectedAvatarId = prefs.getInt("avatarId", R.drawable.a1);
        editAvatarImageView.setImageResource(selectedAvatarId);

        // Initialiser la base de données
        dbHelper = new DatabaseHelper(this);

        // Gestion du clic sur l'avatar
        editAvatarImageView.setOnClickListener(v -> {
            showAvatarSelectionDialog();
        });

        // Gestion du clic sur le bouton Enregistrer
        // Dans saveButton.setOnClickListener :
        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newDisplayName = editDisplayName.getText().toString().trim();

            if (newUsername.isEmpty() || newDisplayName.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateUser(currentUsername, newUsername, newDisplayName, selectedAvatarId)) {
                // 1. Mettre à jour SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("loggedInUsername", newUsername);
                editor.putString("displayName", newDisplayName);
                editor.putInt("avatarId", selectedAvatarId);
                editor.apply();

                // 2. Retourner le résultat AVEC les données
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newUsername", newUsername);
                resultIntent.putExtra("newDisplayName", newDisplayName);
                resultIntent.putExtra("newAvatarId", selectedAvatarId);
                setResult(RESULT_OK, resultIntent);

                // 3. Fermer l'activité
                finish();
            } else {
                Toast.makeText(this, "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Annuler les modifications et fermer
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void showAvatarSelectionDialog() {
        final int[] avatarIds = {
                R.drawable.a1, R.drawable.a2, R.drawable.a3,
                R.drawable.a4, R.drawable.a5, R.drawable.a6,
                R.drawable.a7, R.drawable.a8, R.drawable.a9
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisissez votre avatar");

        // Inflater la vue personnalisée
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_avatar_grid, null);
        GridView gridView = dialogView.findViewById(R.id.avatarGrid);

        // Configurer l'adapter
        AvatarAdapter adapter = new AvatarAdapter(this, avatarIds, selectedAvatarId);
        gridView.setAdapter(adapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            selectedAvatarId = avatarIds[position];
            editAvatarImageView.setImageResource(selectedAvatarId);
            dialog.dismiss();
        });

        dialog.show();
    }
}