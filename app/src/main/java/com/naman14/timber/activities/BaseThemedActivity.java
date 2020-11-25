package com.naman14.timber.activities;

import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.naman14.timber.utils.Helpers;

/**
 * Created by naman on 31/12/15.
 */
public class BaseThemedActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
