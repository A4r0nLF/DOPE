package com.naman14.timber.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.devbrackets.android.exomedia.BuildConfig;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StartVideoStream {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String songURL;
    private VideoView videoView;
    private Context context;

    public StartVideoStream(String songURL, VideoView videoView, Context context) {
        this.songURL = songURL;
        this.videoView = videoView;
        this.context = context;

        startStream();
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
                        Toast.makeText(context, "failed to get stream url", Toast.LENGTH_LONG).show();
                    } else {

                        videoView.setVideoURI(Uri.parse(videoUrl));;
                    }
                }, e -> {
                   Log.e("Error: ", "failed to get stream info", e);

                    Toast.makeText(context, "streaming failed. failed to get stream info", Toast.LENGTH_LONG).show();
                });
        compositeDisposable.add(disposable);
    }
}