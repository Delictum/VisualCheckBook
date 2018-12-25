package com.visualcheckbook.visualcheckbook.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;
import com.visualcheckbook.visualcheckbook.Helpers.CustomSettingsHelper;
import com.visualcheckbook.visualcheckbook.Helpers.LocaleHelper;
import com.visualcheckbook.visualcheckbook.MainActivity;
import com.visualcheckbook.visualcheckbook.OnSwipeTouchListener;
import com.visualcheckbook.visualcheckbook.R;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private LinearLayout mLinearLayout;

    private boolean first = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initCustomModel(view);
        return view;
    }

    private void initCustomModel(View view) {
        initAboutDeveloper(view);
        initSelectLanguage(view);
        initSliding(view);
    }

    private void initAboutDeveloper(View view) {
        Button aboutDeveloper = (Button) view.findViewById(R.id.about_developer);

        aboutDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.title_about_developer)
                        .setMessage(R.string.msg_about_developer)
                        .setIcon(R.drawable.ic_book)
                        .setCancelable(false)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void initSelectLanguage(View view) {
        final Spinner spinner = (Spinner) view.findViewById(R.id.select_language_spinner);

        CharSequence[] entries = getResources().getTextArray(R.array.list_language);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, entries);
        adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setPrompt("Language");

        if (CustomSettingsHelper.mSettings.contains(CustomSettingsHelper.APP_PREFERENCES_LANGUAGE)) {
            spinner.setSelection(CustomSettingsHelper.mSettings.getInt(CustomSettingsHelper.APP_PREFERENCES_LANGUAGE, 0));
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //The solution is incomprehensible situation. At startup, an event is triggered.
                if (first) {
                    first = false;
                    return;
                }

                Locale locale = LocaleHelper.getLocale(position);
                if (locale == null) {
                    return;
                }

                CustomSettingsHelper.setLanguage(position);
                LocaleHelper.setLocale(locale, getActivity());
                ActivityHelper.refreshActivity(getActivity());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void initSliding(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.settings_linear_layout);
        mLinearLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                MainActivity.drawerResult.openDrawer();
            }
        });
    }
}