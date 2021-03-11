package com.naman14.timber.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devbrackets.android.exomedia.BuildConfig;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile;
import com.naman14.timber.R;
import com.naman14.timber.adapters.ExploreAdapter;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.PreferencesUtility;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.naman14.timber.ytmusicapi.OnlineSong;
import com.naman14.timber.ytmusicapi.Parser;
import com.naman14.timber.ytmusicapi.RequestJSON;
import com.naman14.timber.ytmusicapi.YTMusicAPIMain;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class ExploreActivity extends BaseThemedActivity {
    private VideoView videoView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String songURL;
    private String onlinePlaylistID;

    private ExploreAdapter adapter;
    private RecyclerView recyclerView;

    private List<Object> searchResults = Collections.emptyList();

    private Parser parser;
    private RequestJSON requestJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_explore);
        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Intent intent = getIntent();
        songURL = intent.getStringExtra(Constants.SongURL);
        onlinePlaylistID = intent.getStringExtra(Constants.OnlinePlaylistID);

        try {
            YoutubeDL.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e("Error: ", "failed to initialize youtubedl-android", e);
        }

        initViews();
        initListeners();
        startStream();
        initPlaylist();
    }

    private void initPlaylist(){
        parser = new Parser();
        requestJSON = new RequestJSON();
        // search suggestions from Youtube Music API
        ArrayList<Object> objects = new ArrayList<>();
        new YTMusicAPIMain(objects, adapter, parser, requestJSON,3)
                .execute(songURL, onlinePlaylistID);

    }

    private void initViews() {
        videoView = findViewById(R.id.album_art);
        VideoControlsMobile videoControlsMobile = new VideoControlsMobile(this);
        videoView.setControls(videoControlsMobile);

        recyclerView = (RecyclerView) findViewById(R.id.stream_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExploreAdapter(this);
        recyclerView.setAdapter(adapter);

    }

    private void initListeners() {
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.start();
            }
        });
    }



        private void startStream(){



            Disposable disposable = Observable.fromCallable(() -> {
                YoutubeDLRequest request = new YoutubeDLRequest(songURL);
                Log.e("REquest ", ""+ request);
                // best stream containing video+audio
                request.addOption("-f", "best");
                return YoutubeDL.getInstance().getInfo(request);
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(streamInfo -> {


                        String videoUrl = streamInfo.getUrl();
                        if (TextUtils.isEmpty(videoUrl)) {
                            Toast.makeText(ExploreActivity.this, "failed to get stream url", Toast.LENGTH_LONG).show();
                        } else {
                            setupVideoView(videoUrl);
                        }
                    }, e -> {
                        if (BuildConfig.DEBUG) Log.e("Error: ", "failed to get stream info", e);

                        Toast.makeText(ExploreActivity.this, "streaming failed. failed to get stream info", Toast.LENGTH_LONG).show();
                    });
            compositeDisposable.add(disposable);
        }


        private void setupVideoView(String videoUrl) {
            videoView.setVideoURI(Uri.parse(videoUrl));
        }

}
