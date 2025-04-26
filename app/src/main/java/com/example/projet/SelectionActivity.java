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
    private CountDownTimer globalTimer;
    private int globalTimeRemaining = 30;
    private List<QuizQuestion> quizList;
    private QuizQuestion currentQuestion;
    private TextView quizQuestion;
    private LinearLayout quizAnswersLayout;
    private TextView globalTimerTextView;
    private int score = 0;
    private int questionCounter = 0;
    private final int totalQuestions = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtils.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);




        Log.d("DEBUG", "L'activité " + getClass().getSimpleName() + " a été lancée.");

        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout musicButton = findViewById(R.id.musicButton);
        LinearLayout favoriteButton = findViewById(R.id.favoriteButton);
        LinearLayout settingsButton = findViewById(R.id.settingsButton);

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
            startActivity(new Intent(this, FavoritesActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            setActiveButton(settingsButton);
            startActivity(new Intent(this, SettingsActivity.class));
        });

        NavigationHelper.setupNavigationBar(this);
        setActiveButton(musicButton);
        ProfileUtils.setupProfileAvatar(this, R.id.profileAvatar);

        quizQuestion = findViewById(R.id.quizQuestion);
        quizAnswersLayout = findViewById(R.id.quizAnswersLayout);
        globalTimerTextView = findViewById(R.id.globalTimerTextView);
        replayButton = findViewById(R.id.replayButton);

        quizQuestion.setVisibility(View.GONE);
        quizAnswersLayout.setVisibility(View.GONE);
        globalTimerTextView.setVisibility(View.GONE);

        LinearLayout introLayout = findViewById(R.id.introLayout);
        Button startQuizButton = findViewById(R.id.startQuizButton);

        startQuizButton.setOnClickListener(v -> {
            introLayout.setVisibility(View.GONE);
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
        ImageView quizGif = dialogView.findViewById(R.id.sadGif);
        TextView resultText = dialogView.findViewById(R.id.logout_confirmation_text);
        Button replayBtn = dialogView.findViewById(R.id.yes_button);
        Button quitBtn = dialogView.findViewById(R.id.no_button);

        resultText.setText("You got " + score + "/" + totalQuestions + " correct answers!");

        int gifRes = (score >= 3) ? R.drawable.happy : R.drawable.sad;
        Glide.with(this).asGif().load(gifRes).into(quizGif);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        replayBtn.setOnClickListener(view -> {
            animateButtonClick(view);
            score = 0;
            questionCounter = 0;
            globalTimeRemaining = 30;
            startGlobalTimer(globalTimeRemaining);
            showRandomQuestion();
            dialog.dismiss();
        });

        quitBtn.setOnClickListener(view -> {
            animateButtonClick(view);
            view.postDelayed(() -> {
                startActivity(new Intent(SelectionActivity.this, HomeActivity.class));
                finish();
            }, 200);
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        List<LinearLayout> buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.homeButton));
        buttons.add(findViewById(R.id.musicButton));
        buttons.add(findViewById(R.id.favoriteButton));
        buttons.add(findViewById(R.id.settingsButton));

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
            // Récupérer la langue actuelle
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String currentLang = prefs.getString("locale", "fr");

            String filename;
            if ("fr".equals(currentLang)) {
                filename = "quiz_data_fr.json"; // Si français
            } else {
                filename = "quiz_data.json"; // Sinon anglais par défaut
            }

            InputStreamReader reader = new InputStreamReader(getAssets().open(filename));
            Type questionListType = new TypeToken<List<QuizQuestion>>() {}.getType();
            quizList = new Gson().fromJson(reader, questionListType);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de chargement des questions", Toast.LENGTH_SHORT).show();
        }
    }


    private void startGlobalTimer(int seconds) {
        globalTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                globalTimeRemaining = (int) (millisUntilFinished / 1000);
                globalTimerTextView.setText("Time remaining : " + globalTimeRemaining + "s");
            }

            @Override
            public void onFinish() {
                globalTimerTextView.setText("Time elapsed !");
                showResultsDialog();
            }
        };
        globalTimer.start();
    }

    private void showRandomQuestion() {
        quizAnswersLayout.removeAllViews();
        Random random = new Random();
        currentQuestion = quizList.get(random.nextInt(quizList.size()));

        quizQuestion.setText(currentQuestion.question);

        for (int i = 0; i < currentQuestion.answers.length; i++) {
            Button answerButton = new Button(this);
            answerButton.setText(currentQuestion.answers[i]);
            answerButton.setTextColor(getResources().getColor(android.R.color.white));
            answerButton.setBackgroundResource(R.drawable.custom_back_button);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10);
            answerButton.setLayoutParams(params);
            answerButton.setPadding(30, 20, 30, 20);

            int finalI = i;
            answerButton.setOnClickListener(v -> {
                if (finalI == currentQuestion.correctIndex) {
                    score++;
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
            showRandomQuestion();
        } else {
            displayResults();
        }
    }

    private void displayResults() {
        if (globalTimer != null) {
            globalTimer.cancel();
        }
        replayButton.setVisibility(View.GONE);
        showResultsDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView profileAvatar = findViewById(R.id.profileAvatar);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        profileAvatar.setImageResource(prefs.getInt("avatarId", R.drawable.a1));
    }

    private static class QuizQuestion {
        String question;
        String[] answers;
        int correctIndex;
        String audioFile;

        public QuizQuestion(String question, String[] answers, int correctIndex) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
        }

        public QuizQuestion(String question, String[] answers, int correctIndex, String audioFile) {
            this.question = question;
            this.answers = answers;
            this.correctIndex = correctIndex;
            this.audioFile = audioFile;
        }
    }
}
