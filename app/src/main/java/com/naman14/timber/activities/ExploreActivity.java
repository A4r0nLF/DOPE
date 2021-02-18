package com.naman14.timber.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.naman14.timber.R;
import com.naman14.timber.utils.PreferencesUtility;

public class ExploreActivity extends BaseThemedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_explore);
        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


    }
}
