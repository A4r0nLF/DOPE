package com.naman14.timber.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;


import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;


import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;


import com.devbrackets.android.exomedia.ui.widget.VideoView;


public class DownloadSong {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String songURL;

    private Context context;
    private boolean downloading = false;
    private int progress;
    private DownloadButtonAnimation downloadButtonAnimation;


    public DownloadSong(String songURL, Context context, DownloadButtonAnimation downloadButtonAnimation) {
        progress = 10;
        this.downloadButtonAnimation = downloadButtonAnimation;
        this.songURL = songURL;
        this.context = context;
        try {
            FFmpeg.getInstance().init(context);
        } catch (YoutubeDLException e) {
            e.printStackTrace();
        }
        startDownload();
    }

    private void startDownload() {
        if (downloading) {
            Toast.makeText(context, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }
        downloadButtonAnimation.startDownload();
        String url = songURL;

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_MUSIC
                + "/JustDownloaded/";
        request.addOption("-o", musicPath + "%(artist)s-%(title)s.%(ext)s");
        request.addOption("--extract-audio");
        request.addOption("--audio-format", "mp3");
        request.addOption("--embed-thumbnail");
        request.addOption("--add-metadata");
        request.addOption("--ppa", "EmbedThumbnail+ffmpeg_o:-c:v png -vf crop=\"'if(gt(ih,iw),iw,ih)':'if(gt(iw,ih),ih,iw)'\"");
        request.addOption("--verbose");


        Toast.makeText(context, "download started", Toast.LENGTH_LONG).show();
        ;

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    writeID3Meta();
                    downloadButtonAnimation.finishDownloadSuccessfully();
                    downloadButtonAnimation.getProgressBar().setProgress(100);
                    Log.e("Download Result", youtubeDLResponse.getOut());
                    downloading = false;
                }, e -> {
                    Log.e("Error", "failed to download", e);

                    downloadButtonAnimation.finishDownloadFailed();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }

    private void writeID3Meta() {

    }


    private final DownloadProgressCallback callback = new DownloadProgressCallback() {

        @Override
        public void onProgressUpdate(float progress, long etaInSeconds, String line) {
            downloadButtonAnimation.getProgressBar().setProgress((int) progress + 20);
        }
    };

}
