package com.example.projet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectionActivity extends BaseActivity {
    private Button replayButton;

    private CountDownTimer countDownTimer;
    private CountDownTimer globalTimer;  // Chrono global pour l'ensemble du quiz
    private int globalTimeRemaining = 30;  // Temps total du quiz (20 secondes)
    private List<QuizQuestion> quizList;
    private QuizQuestion currentQuestion;
    private TextView quizQuestion;
    private LinearLayout quizAnswersLayout;
    private TextView globalTimerTextView;  // TextView pour afficher le chrono global
    private int score = 0;
    private int questionCounter = 0;
    private final int totalQuestions = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "L'activité " + getClass().getSimpleName() + " a été lancée.");
        setContentView(R.layout.activity_selection);

        // Initialiser les éléments d'interface utilisateur
        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        // Configurer les boutons de navigation
        homeButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(homeButton);
            startActivity(new Intent(this, HomeActivity.class));
        });

        musicButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(musicButton);
        });

        favoriteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Clic Favoris", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SelectionActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(settingsButton);
            startActivity(new Intent(this, SettingsActivity.class));
        });

        NavigationHelper.setupNavigationBar(this);
        setActiveButton(musicButton);
        ProfileUtils.setupProfileAvatar(this, R.id.profileAvatar);

       /* quizQuestion = findViewById(R.id.quizQuestion);
        quizAnswersLayout = findViewById(R.id.quizAnswersLayout);

        // Récupérer le TextView pour afficher le chrono global
        globalTimerTextView = findViewById(R.id.globalTimerTextView);

        // Charger les questions à partir du fichier JSON
        loadQuizQuestions();

        // Démarrer le chrono global pour 20 secondes
        startGlobalTimer(globalTimeRemaining);

        // Afficher la première question
        showRandomQuestion();
        replayButton = findViewById(R.id.replayButton);*/
        quizQuestion = findViewById(R.id.quizQuestion);
        quizAnswersLayout = findViewById(R.id.quizAnswersLayout);
        globalTimerTextView = findViewById(R.id.globalTimerTextView);
        replayButton = findViewById(R.id.replayButton);

        // Cacher les éléments du quiz
        quizQuestion.setVisibility(View.GONE);
        quizAnswersLayout.setVisibility(View.GONE);
        globalTimerTextView.setVisibility(View.GONE);

        // Montrer l'intro
        LinearLayout introLayout = findViewById(R.id.introLayout);
        Button startQuizButton = findViewById(R.id.startQuizButton);

        startQuizButton.setOnClickListener(v -> {
            introLayout.setVisibility(View.GONE);

            // Montrer les éléments du quiz
            quizQuestion.setVisibility(View.VISIBLE);
            quizAnswersLayout.setVisibility(View.VISIBLE);
            globalTimerTextView.setVisibility(View.VISIBLE);

            loadQuizQuestions();
            startGlobalTimer(globalTimeRemaining);
            showRandomQuestion();
        });

    }
    private void showResultsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quiz_result, null);
        ImageView quizGif = dialogView.findViewById(R.id.sadGif);  // Change l'ID si nécessaire
        TextView resultText = dialogView.findViewById(R.id.logout_confirmation_text);
        Button replayBtn = dialogView.findViewById(R.id.yes_button);
        Button quitBtn = dialogView.findViewById(R.id.no_button);

        // Définir le texte du résultat
        resultText.setText("Tu as eu " + score + "/" + totalQuestions + " bonnes réponses ! 🎉");

        // Charger un GIF selon le résultat
        int gifRes = (score >= 3) ? R.drawable.happy : R.drawable.sad; // à adapter selon ton projet
        Glide.with(this)
                .asGif()
                .load(gifRes)
                .into(quizGif);

        // Créer le dialog personnalisé
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Rejouer
        replayBtn.setOnClickListener(view -> {
            animateButtonClick(view);
            score = 0;
            questionCounter = 0;
            globalTimeRemaining = 20;
            startGlobalTimer(globalTimeRemaining);
            showRandomQuestion();
            dialog.dismiss();
        });

        // Quitter
        quitBtn.setOnClickListener(view -> {
            animateButtonClick(view);
            view.postDelayed(() -> {
                startActivity(new Intent(SelectionActivity.this, HomeActivity.class));
                finish();
            }, 200);
        });

        dialog.show();

        // Ajuster la taille du Dialog
        dialog.getWindow().setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,  // Largeur : contenu seulement
                LinearLayout.LayoutParams.WRAP_CONTENT   // Hauteur : contenu seulement
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    private void animateButtonClick(View button) {
        button.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }



    private void setActiveButton(LinearLayout activeButton) {
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

        List<LinearLayout> buttons = new ArrayList<>();
        buttons.add(homeButton);
        buttons.add(musicButton);
        buttons.add(favoriteButton);
        buttons.add(settingsButton);

        for (LinearLayout button : buttons) {
            button.setBackground(null);
            if (button == activeButton) {
                button.setAlpha(1.0f);
                button.setBackgroundResource(R.drawable.nav_button_background_selected);
            } else {
                button.setAlpha(0.85f);
                button.setBackgroundResource(R.drawable.nav_button_background);
            }
        }
    }

    private void loadQuizQuestions() {
        try {
            // Lire les questions à partir du fichier JSON
            InputStreamReader reader = new InputStreamReader(getAssets().open("quiz_data.json"));
            Type questionListType = new TypeToken<List<QuizQuestion>>() {}.getType();
            quizList = new Gson().fromJson(reader, questionListType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGlobalTimer(int seconds) {
        globalTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Afficher le temps restant pour le quiz entier
                globalTimeRemaining = (int) (millisUntilFinished / 1000);
                globalTimerTextView.setText("Temps global restant: " + globalTimeRemaining);
            }

            @Override
            public void onFinish() {
                // Lorsque le temps global est écoulé, afficher les résultats
                globalTimerTextView.setText("Temps écoulé !");
                showResultsDialog(); // Afficher la boîte de dialogue
            }
        };
        globalTimer.start();  // Démarrer le chrono global
    }

    private void showRandomQuestion() {
        quizAnswersLayout.removeAllViews();
        Random random = new Random();
        currentQuestion = quizList.get(random.nextInt(quizList.size()));  // Choix aléatoire d'une question

        quizQuestion.setText(currentQuestion.question);

        for (int i = 0; i < currentQuestion.answers.length; i++) {
            Button answerButton = new Button(this);
            answerButton.setText(currentQuestion.answers[i]);
            answerButton.setTextColor(getResources().getColor(android.R.color.white));
            answerButton.setBackgroundResource(R.drawable.custom_back_button);

            // Définir des marges pour espacer verticalement les boutons
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10); // marges haut et bas
            answerButton.setLayoutParams(params);

// Ajouter du padding pour que le texte respire
            answerButton.setPadding(30, 20, 30, 20);

            answerButton.setLayoutParams(params);

            int finalI = i;
            answerButton.setOnClickListener(v -> {
                if (finalI == currentQuestion.correctIndex) {
                    score++;  // Bonne réponse
                    Toast.makeText(this, "🎉", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "❌", Toast.LENGTH_SHORT).show();
                }

                questionCounter++;
                nextQuestion();
            });

            quizAnswersLayout.addView(answerButton);
        }
    }


    private void nextQuestion() {
        if (questionCounter < totalQuestions) {
            showRandomQuestion();  // Afficher la question suivante
        } else {
            displayResults();  // Afficher les résultats si c'est la dernière question
        }
    }

    private void displayResults() {
        // Stop global timer
        if (globalTimer != null) {
            globalTimer.cancel();
        }

        // Masquer le bouton "Rejouer" avant d'afficher le pop-up
        Button replayButton = findViewById(R.id.replayButton);
        replayButton.setVisibility(View.GONE);  // Cacher le bouton "Rejouer" avant d'afficher le pop-up

        // Afficher la boîte de dialogue des résultats
        showResultsDialog(); // Afficher le pop-up pour les résultats
    }


    @Override
    protected void onResume() {
        super.onResume();
        ImageView profileAvatar = findViewById(R.id.profileAvatar);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
    }

    // Classe représentant une question du quiz
    private static class QuizQuestion {
        String question;
        String[] answers;
        int correctIndex;
        String audioFile; // Optionnel pour les questions avec audio

        // Constructeur pour les questions sans audio
        public QuizQuestion(String question, String[] answers, int correctIndex) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
        }

        // Constructeur pour les questions avec audio
        public QuizQuestion(String question, String[] answers, int correctIndex, String audioFile) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
            this.audioFile = audioFile;
        }
    }
}
