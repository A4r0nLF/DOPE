package com.naman14.timber.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naman14.timber.R;

public class DownloadButtonAnimation {

    private Context context;

    private FloatingActionButton downloadButton;
    private ProgressBar progressBar;

    public DownloadButtonAnimation(Context context) {
        this.context = context;
        downloadButton = ((Activity) context).findViewById(R.id.download);
        progressBar = ((Activity) context).findViewById(R.id.download_progress_bar);
    }

    public void startDownload(){
        disableButton();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(10);
    }

    public void finishDownloadSuccessfully(){
        downloadButton.setImageResource(R.drawable.ic_download_done);
    }

    public void finishDownloadFailed(){
        progressBar.setVisibility(View.GONE);
        downloadButton.setImageResource(R.drawable.ic_download_failed);
    }

    public void resetButton(){
        progressBar.setVisibility(View.GONE);
        downloadButton.setImageResource(R.drawable.ic_start_download_white);
        enableButton();
    }

    private void disableButton(){
        downloadButton.setClickable(false);
    }

    private void enableButton(){
        downloadButton.setClickable(true);
    }



    public FloatingActionButton getDownloadButton() {
        return downloadButton;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
