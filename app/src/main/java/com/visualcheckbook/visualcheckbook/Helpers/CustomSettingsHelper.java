package com.visualcheckbook.visualcheckbook.Helpers;

import android.content.SharedPreferences;

public final class CustomSettingsHelper {

    public static SharedPreferences mSettings;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_LANGUAGE = "language";

    public static void setLanguage(int languagePosition) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LANGUAGE, languagePosition);
        editor.apply();
    }
}
