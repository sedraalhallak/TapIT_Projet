package com.example.projet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Locale;

public class BaseActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String langCode = prefs.getString("app_language", "en");

        Context context = updateBaseContextLocale(newBase, langCode);
        super.attachBaseContext(context);
    }

    private static Context updateBaseContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            MusicManager.release(); // Arrête la musique uniquement quand l'app est fermée
        }
    }
}
