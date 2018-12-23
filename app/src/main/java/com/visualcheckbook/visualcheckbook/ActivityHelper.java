package com.visualcheckbook.visualcheckbook;

import android.content.Context;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

final class ActivityHelper {

    static IDrawerItem[] initDrawerItems(Integer currentActivity) {
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

    static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
