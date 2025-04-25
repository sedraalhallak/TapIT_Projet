package com.example.projet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class EditProfileActivity extends BaseActivity {

    private EditText editUsername;
    private Button saveButton;
    private ImageView editAvatarImageView;
    private DatabaseHelper dbHelper;
    private String currentUsername;
    private int selectedAvatarId = R.drawable.a1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // View initialize
        editAvatarImageView = findViewById(R.id.editAvatarImageView);
        editUsername = findViewById(R.id.editUsername);
        saveButton = findViewById(R.id.saveButton);
        EditText bioField = findViewById(R.id.editBio);


        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("loggedInUsername", "");


        editUsername.setText(currentUsername);
        bioField.setText(prefs.getString("bio", ""));
        selectedAvatarId = prefs.getInt("avatarId", R.drawable.a1);
        editAvatarImageView.setImageResource(selectedAvatarId);

        // database
        dbHelper = new DatabaseHelper(this);


        editAvatarImageView.setOnClickListener(v -> {
            showAvatarSelectionDialog();
        });


        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newBio = bioField.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // update database
            if (dbHelper.updateUser(currentUsername, newUsername, "", selectedAvatarId, newBio)) {

                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putString("loggedInUsername", newUsername);
                editor.putInt("avatarId", selectedAvatarId);
                editor.putString("bio", newBio);
                editor.apply();


                Intent resultIntent = new Intent();
                resultIntent.putExtra("newUsername", newUsername);
                resultIntent.putExtra("newAvatarId", selectedAvatarId);
                setResult(RESULT_OK, resultIntent);
                Snackbar.make(saveButton, "Profil mis à jour avec succès", Snackbar.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(this, "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
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

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_avatar_grid, null);
        GridView gridView = dialogView.findViewById(R.id.avatarGrid);

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
