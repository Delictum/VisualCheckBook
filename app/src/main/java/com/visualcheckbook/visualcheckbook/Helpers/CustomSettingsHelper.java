package com.visualcheckbook.visualcheckbook.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public final class CustomSettingsHelper {

    public static SharedPreferences mSettings;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_LANGUAGE = "language";
    public static final String APP_PREFERENCES_START_SCREEN = "startScreen";

    public static void setLanguage(int languagePosition) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_LANGUAGE, languagePosition);
        editor.apply();
    }

    public static void setStartScreen(int startScreenPosition) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_START_SCREEN, startScreenPosition);
        editor.apply();
    }

    public static int getPositionStartScreen(Activity activity) {
        CustomSettingsHelper.mSettings = activity.getSharedPreferences(CustomSettingsHelper.APP_PREFERENCES, Context.MODE_PRIVATE);

        int screenPosition = 0;
        if (CustomSettingsHelper.mSettings.contains(CustomSettingsHelper.APP_PREFERENCES_START_SCREEN)) {
            screenPosition = CustomSettingsHelper.mSettings.getInt(CustomSettingsHelper.APP_PREFERENCES_START_SCREEN, 0);
        }
        return screenPosition;
    }
}
