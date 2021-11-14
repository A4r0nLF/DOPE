package com.naman14.timber.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naman14.timber.R;
import com.naman14.timber.adapters.ExploreAdapter;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.DownloadSong;
import com.naman14.timber.utils.PreferencesUtility;


import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.naman14.timber.utils.StartVideoStream;

import com.naman14.timber.ytmusicapi.Parser;
import com.naman14.timber.ytmusicapi.RequestJSON;
import com.naman14.timber.ytmusicapi.YTMusicAPIMain;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;





public class ExploreActivity extends BaseThemedActivity {
    private VideoView videoView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String songURL;
    private String onlinePlaylistID;

    private ExploreAdapter adapter;
    private RecyclerView recyclerView;

    private FloatingActionButton downloadButton;
    private ProgressBar progressBar;

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
            FFmpeg.getInstance().init(getApplication());
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
        adapter = new ExploreAdapter(this, videoView);
        recyclerView.setAdapter(adapter);


        downloadButton = findViewById(R.id.download);
        progressBar = findViewById(R.id.download_progress_bar);
    }

    private void initListeners() {
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.start();
            }
        });

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExploreActivity.this, "download started", Toast.LENGTH_LONG).show();
                DownloadSong downloadSong = new DownloadSong(songURL, getApplication(), progressBar);
            }
        });
    }


    private void startStream(){

        StartVideoStream startVideoStream = new StartVideoStream(songURL, videoView, this);

    }

        private void setupVideoView(String videoUrl) {
         //   videoView.setVideoURI(Uri.parse(videoUrl));
        }

    public String getSongURL() {
        return songURL;
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
