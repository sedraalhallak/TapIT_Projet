package com.example.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        EditText nameInput = findViewById(R.id.name); // name
        EditText emailInput = findViewById(R.id.email); // email
        EditText passwordInput = findViewById(R.id.signup_password); // password


        findViewById(R.id.btn_signup).setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(SignupActivity.this);
            if (dbHelper.addUser(name, email, password)) {
                Toast.makeText(SignupActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SignupActivity.this, "Email déjà utilisé.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_back_to_login).setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}