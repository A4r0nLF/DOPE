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
import com.naman14.timber.utils.DownloadButtonAnimation;
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
    private String[] currentSongMeta;


    private ExploreAdapter adapter;
    private RecyclerView recyclerView;

    private DownloadButtonAnimation downloadButtonAnimation;
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
        currentSongMeta = new String[4];
        currentSongMeta[0] = intent.getStringExtra(Constants.SongURL);
        currentSongMeta[1] = intent.getStringExtra(Constants.SongTitle);
        currentSongMeta[2] = intent.getStringExtra(Constants.Artistname);
        currentSongMeta[3] = intent.getStringExtra(Constants.OnlinePlaylistID);

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
                .execute(currentSongMeta[0] , currentSongMeta[3]);

    }

    private void initViews() {
        videoView = findViewById(R.id.album_art);
        VideoControlsMobile videoControlsMobile = new VideoControlsMobile(this);
        videoView.setControls(videoControlsMobile);
        recyclerView = (RecyclerView) findViewById(R.id.stream_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExploreAdapter(this, videoView, currentSongMeta[0] );
        recyclerView.setAdapter(adapter);

        downloadButtonAnimation = new DownloadButtonAnimation(ExploreActivity.this);

    }

    private void initListeners() {
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.start();
            }
        });

        downloadButtonAnimation.getDownloadButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExploreActivity.this, "download started", Toast.LENGTH_LONG).show();
                DownloadSong downloadSong = new DownloadSong(currentSongMeta , getApplication(), downloadButtonAnimation);
                downloadSong.startDownload();
            }
        });
    }


    private void startStream(){

        StartVideoStream startVideoStream = new StartVideoStream(currentSongMeta[0] , videoView, this);

    }

    private void setupVideoView(String videoUrl) {
        //   videoView.setVideoURI(Uri.parse(videoUrl));
    }

    public String getSongURL() {
        return currentSongMeta[0] ;
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
