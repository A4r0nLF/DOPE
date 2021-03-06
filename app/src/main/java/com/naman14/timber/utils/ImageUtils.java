/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.models.Album;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

public class ImageUtils {
    private static final DisplayImageOptions lastfmDisplayImageOptions =
                                                new DisplayImageOptions.Builder()
                                                        .cacheInMemory(true)
                                                        .cacheOnDisk(true)
                                                        .showImageOnFail(R.drawable.ic_empty_music2)
                                                        .build();

    private static final DisplayImageOptions diskDisplayImageOptions =
                                                new DisplayImageOptions.Builder()
                                                        .cacheInMemory(true)
                                                        .build();

    public static Bitmap cropImgToSquare(Bitmap img){
        int startX = 0;
        int startY = 0;
        int width = 0;
        int height = 0;
        height = img.getHeight();
        width = height;
        startX = (img.getWidth() - height) / 2;
        Bitmap crpdImg = Bitmap.createBitmap(img, startX, startY, width, height );
        return crpdImg;
    }

    public static Bitmap getBitmapFromByteArray(byte[] imageData){
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }



    public static byte[] getJpegByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static void loadAlbumArtIntoView(final long albumId, final ImageView view) {
        loadAlbumArtIntoView(albumId, view, new SimpleImageLoadingListener());
    }

    public static void loadAlbumArtIntoView(final long albumId, final ImageView view,
                                            final ImageLoadingListener listener) {
        if (PreferencesUtility.getInstance(view.getContext()).alwaysLoadAlbumImagesFromLastfm()) {
            loadAlbumArtFromLastfm(albumId, view, listener);
        } else {
            loadAlbumArtFromDiskWithLastfmFallback(albumId, view, listener);
        }
    }

    private static void loadAlbumArtFromDiskWithLastfmFallback(final long albumId, ImageView view,
                                                               final ImageLoadingListener listener) {
        ImageLoader.getInstance()
                .displayImage(TimberUtils.getAlbumArtUri(albumId).toString(),
                              view,
                              diskDisplayImageOptions,
                              new SimpleImageLoadingListener() {
                                  @Override
                                  public void onLoadingFailed(String imageUri, View view,
                                                              FailReason failReason) {
                                      loadAlbumArtFromLastfm(albumId, (ImageView) view, listener);
                                      listener.onLoadingFailed(imageUri, view, failReason);
                                  }

                                  @Override
                                  public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                      listener.onLoadingComplete(imageUri, view, loadedImage);
                                  }
                              });
    }

    private static void loadAlbumArtFromLastfm(long albumId, final ImageView albumArt, final ImageLoadingListener listener) {
        Album album = AlbumLoader.getAlbum(albumArt.getContext(), albumId);
    }

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final androidx.renderscript.Allocation input = androidx.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final androidx.renderscript.Allocation output = androidx.renderscript.Allocation.createTyped(rs, input.getType());
        final androidx.renderscript.ScriptIntrinsicBlur script = androidx.renderscript.ScriptIntrinsicBlur.create(rs, androidx.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }
}
