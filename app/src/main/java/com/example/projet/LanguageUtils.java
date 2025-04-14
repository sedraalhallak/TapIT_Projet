package com.example.projet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    public static void setLocale(Activity activity, String langCode) {
        // Mettre à jour la langue dans les préférences partagées
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_language", langCode);
        editor.apply();

        // Configurer la langue pour l'application
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = activity.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        activity.getResources().updateConfiguration(config, dm);
    }

    public static String getSavedLanguage(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
        return prefs.getString("app_language", "en"); // Default is English
    }
}
