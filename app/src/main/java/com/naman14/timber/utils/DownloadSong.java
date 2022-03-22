package com.naman14.timber.utils;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;


import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;


public class DownloadSong extends AsyncTask<Void, Integer, Boolean> {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String[] currentSongMeta;
    private final String musicPath;

    private Context context;
    private boolean subThreadFinished;
    private int progress;
    private DownloadButtonAnimation downloadButtonAnimation;


    public DownloadSong(String[] currentSongMeta, Context context, DownloadButtonAnimation downloadButtonAnimation) {

        this.downloadButtonAnimation = downloadButtonAnimation;
        this.currentSongMeta = currentSongMeta;
        musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_MUSIC
                + "/JustDownloaded/";
        subThreadFinished = false;

        this.context = context;
        try {
            FFmpeg.getInstance().init(context);
        } catch (YoutubeDLException e) {
            e.printStackTrace();
        }
        downloadButtonAnimation.startDownload();
        downloadButtonAnimation.getProgressBar().setProgress(10);


    }

    @Override
    protected Boolean doInBackground(Void... voids) {

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
                    downloadButtonAnimation.getProgressBar().setProgress(80);
                    writeID3Meta();
                    downloadButtonAnimation.getProgressBar().setProgress(100);
                    downloadButtonAnimation.finishDownloadSuccessfully();
                    subThreadFinished = true;
                    Log.e("subthreadfinished", "haha");
                }, e -> {
                    Log.e("Error", "failed to download", e);
                    downloadButtonAnimation.finishDownloadFailed();
                    subThreadFinished = true;
                });
        compositeDisposable.add(disposable);
        while (!subThreadFinished){}
        Log.e("mainthreadfinished", "haha");
        return true;
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

            MediaScannerConnection.scanFile(context,
                    new String[]{musicPath + currentSongMeta[2] + " - " + currentSongMeta[1] + ".mp3"},
                    new String[]{"audio/mp3", "*/*"},
                    (s, uri) -> { }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final DownloadProgressCallback callback = new DownloadProgressCallback() {

        @Override
        public void onProgressUpdate(float progress, long etaInSeconds, String line) {
            downloadButtonAnimation.getProgressBar().setProgress((int)(20 + (progress/100*70)));
        }
    };


}
