package com.naman14.timber.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class UpdateYoutubeDL {

    private boolean updateInProgress;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public UpdateYoutubeDL(Context context) throws YoutubeDLException {
        YoutubeDL.getInstance().init(context);
        updateYoutubeDL(context);
    }

    private void updateYoutubeDL(Context appContext) {
        if (updateInProgress) {
            Toast.makeText(appContext, "update is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        updateInProgress = true;

        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().updateYoutubeDL(appContext))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {

                    switch (status) {
                        case DONE:
                            Toast.makeText(appContext, "update successful", Toast.LENGTH_LONG).show();
                            break;
                        case ALREADY_UP_TO_DATE:
                            Toast.makeText(appContext, "already up to date", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(appContext, status.toString(), Toast.LENGTH_LONG).show();
                            break;
                    }
                    updateInProgress = false;
                }, e -> {
                    Log.e("Error", "failed to update", e);
                    Toast.makeText(appContext, "update failed", Toast.LENGTH_LONG).show();
                    updateInProgress = false;
                });
        compositeDisposable.add(disposable);
    }

}
