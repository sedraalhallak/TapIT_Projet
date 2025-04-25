package com.example.projet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    public static void applySavedLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String locale = prefs.getString("locale", "fr");
        setLocale(context, locale);
    }

    public static void setLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
