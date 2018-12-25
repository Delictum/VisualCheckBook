package com.visualcheckbook.visualcheckbook.Helpers;

import android.app.Activity;
import android.content.res.Configuration;

import com.visualcheckbook.visualcheckbook.R;

import java.util.Locale;

public final class LocaleHelper {

    public static void setLocale(Locale locale, Activity activity) {

        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;

        activity.getResources().updateConfiguration(configuration, null);
        activity.setTitle(R.string.app_name);
    }

    public static Locale getLocale(int position) {

        switch (position) {
            case 0: {
                return new Locale("en");
            }
            case 1: {
                return new Locale("ru");
            }
        }

        return null;
    }
}
