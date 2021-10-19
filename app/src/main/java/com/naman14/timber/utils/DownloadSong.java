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

    public DownloadSong(String songURL, Context context) {
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


        Toast.makeText(context, "download started", Toast.LENGTH_LONG).show();;

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {

                    Toast.makeText(context, "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                     Log.e("Error",  "failed to download", e);

                    Toast.makeText(context, "download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }




    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "youtubedl-android");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }


    private final DownloadProgressCallback callback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {

                        Log.i("Download Progress", String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");

        }
    };

}
