package com.visualcheckbook.visualcheckbook.Helpers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.visualcheckbook.visualcheckbook.R;

public final class ActivityHelper {

    public static IDrawerItem[] initDrawerItems(Integer currentActivity) {
        return new IDrawerItem[] {
                new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_home)
                        .withIcon(FontAwesome.Icon.faw_camera)
                        .setEnabled(currentActivity == 0 ? false : true),
                new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_library_book)
                        .withIcon(FontAwesome.Icon.faw_book)
                        .setEnabled(currentActivity == 1 ? false : true),
                new DividerDrawerItem(),
                new SecondaryDrawerItem()
                        .withName(R.string.drawer_item_settings)
                        .withIcon(FontAwesome.Icon.faw_cog)
                        .setEnabled(currentActivity == 2 ? false : true),
                new SecondaryDrawerItem()
                        .withName(R.string.drawer_item_help)
                        .withIcon(FontAwesome.Icon.faw_question)
                        .setEnabled(currentActivity == 3 ? false : true),
                new DividerDrawerItem(),
                new SecondaryDrawerItem()
                        .withName(R.string.drawer_item_contact)
                        .withIcon(FontAwesome.Icon.faw_mail_forward)
                        .withBadge("12+").withIdentifier(1)};
    }

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void refreshActivity(Activity activity) {

        activity.finish();
        activity.startActivity(activity.getIntent());
    }

    public static void initLocaleHelper(Activity activity) {
        CustomSettingsHelper.mSettings = activity.getSharedPreferences(CustomSettingsHelper.APP_PREFERENCES, Context.MODE_PRIVATE);

        int localePosition = 0;
        if (CustomSettingsHelper.mSettings.contains(CustomSettingsHelper.APP_PREFERENCES_LANGUAGE)) {
            localePosition = CustomSettingsHelper.mSettings.getInt(CustomSettingsHelper.APP_PREFERENCES_LANGUAGE, 0);
        }
        LocaleHelper.setLocale(LocaleHelper.getLocale(localePosition), activity);
    }
}
