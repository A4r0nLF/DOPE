package com.naman14.timber.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;


import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;


import com.devbrackets.android.exomedia.ui.widget.VideoView;


public class DownloadSong {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String[] currentSongMeta;
    private final String musicPath;

    private Context context;
    private boolean downloading = false;
    private int progress;
    private DownloadButtonAnimation downloadButtonAnimation;


    public DownloadSong(String[] currentSongMeta, Context context, DownloadButtonAnimation downloadButtonAnimation) {
        progress = 10;
        this.downloadButtonAnimation = downloadButtonAnimation;
        this.currentSongMeta = currentSongMeta;
        musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_MUSIC
                + "/JustDownloaded/";

        this.context = context;
        try {
            FFmpeg.getInstance().init(context);
        } catch (YoutubeDLException e) {
            e.printStackTrace();
        }
    }




    public void startDownload() {
        if (downloading) {
            Toast.makeText(context, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }
        downloading = true;
        downloadButtonAnimation.startDownload();
        String url = currentSongMeta[0];

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.addOption("-o", musicPath + "tmpDownload.%(ext)s");
        request.addOption("--extract-audio");
        request.addOption("--audio-format", "mp3");
        request.addOption("--embed-thumbnail");
        request.addOption("--add-metadata");


        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    downloadButtonAnimation.getProgressBar().setProgress(100);
                    writeID3Meta();
                    downloadButtonAnimation.finishDownloadSuccessfully();
                    downloadButtonAnimation.getProgressBar().setProgress(100);

                    downloading = false;
                }, e -> {
                    Log.e("Error", "failed to download", e);

                    downloadButtonAnimation.finishDownloadFailed();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }

    private void writeID3Meta() {
        try {
            Mp3File mp3file = new Mp3File(musicPath + "tmpDownload.mp3");


            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                byte[] imageData = id3v2Tag.getAlbumImage();
                Bitmap albumCover = ImageUtils.getBitmapFromByteArray(imageData);
                albumCover = ImageUtils.cropImgToSquare(albumCover);
                mp3file.getId3v2Tag().clearAlbumImage();
                mp3file.getId3v2Tag().setAlbumImage(ImageUtils.getJpegByteArrayFromBitmap(albumCover), "image/jpg");
            }

            mp3file.save(musicPath + currentSongMeta[2] + " - " + currentSongMeta[1]+ ".mp3");
            File fdelete = new File(musicPath + "tmpDownload.mp3");
            if (fdelete.exists()) fdelete.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final DownloadProgressCallback callback = new DownloadProgressCallback() {

        @Override
        public void onProgressUpdate(float progress, long etaInSeconds, String line) {
            downloadButtonAnimation.getProgressBar().setProgress((int) progress + 20);
        }
    };

}
