/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;


import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.naman14.timber.R;
import com.naman14.timber.fragments.SettingsFragment;
import com.naman14.timber.subfragments.StyleSelectorFragment;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.PreferencesUtility;

public class SettingsActivity extends BaseThemedActivity implements ColorChooserDialog.ColorCallback {

    private String action;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (PreferencesUtility.getInstance(this).getTheme().equals("dark")) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        action = getIntent().getAction();

        if (action.equals(Constants.SETTINGS_STYLE_SELECTOR)) {
            getSupportActionBar().setTitle(R.string.now_playing);
            String what = getIntent().getExtras().getString(Constants.SETTINGS_STYLE_SELECTOR_WHAT);
            Fragment fragment = StyleSelectorFragment.newInstance(what);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else {
            getSupportActionBar().setTitle(R.string.settings);
            PreferenceFragment fragment = new SettingsFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {

        recreate(); // recreation needed to reach the checkboxes in the preferences layout
    }

}
