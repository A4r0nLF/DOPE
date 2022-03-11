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

package com.naman14.timber.adapters;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naman14.timber.R;
import com.naman14.timber.dataloaders.LoadPicFromURL;


import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.DownloadButtonAnimation;
import com.naman14.timber.utils.DownloadSong;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.StartVideoStream;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.ytmusicapi.OnlineSong;

import com.devbrackets.android.exomedia.ui.widget.VideoView;


import java.net.URI;
import java.util.Collections;
import java.util.List;

public class ExploreAdapter extends BaseSongAdapter<ExploreAdapter.ItemHolder> {

    private Activity mContext;
    private List relatedPlaylist = Collections.emptyList();
    private VideoView videoView;
    private DownloadButtonAnimation downloadButtonAnimation;

    public ExploreAdapter(Activity context, VideoView videoView, DownloadButtonAnimation downloadButtonAnimation) {
        this.mContext = context;
        this.videoView = videoView;
        this.downloadButtonAnimation = downloadButtonAnimation;
    }

    @Override
    public ExploreAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 10:
                View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_section_header, null);
                ExploreAdapter.ItemHolder ml10 = new ExploreAdapter.ItemHolder(v10);
                return ml10;
            case 11:
                View v11 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_online, null);
                ExploreAdapter.ItemHolder ml11 = new ExploreAdapter.ItemHolder(v11);
                return ml11;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final ExploreAdapter.ItemHolder itemHolder, int i) {
        switch (getItemViewType(i)) {

            case 10:
                itemHolder.sectionHeader.setText((String) relatedPlaylist.get(i));
                break;
            case 11:
                OnlineSong songOnline = (OnlineSong) relatedPlaylist.get(i);
                itemHolder.title.setText(songOnline.title);
                String subtitle = songOnline.artistName + " - " + songOnline.albumName;
                itemHolder.songartist.setText(subtitle);

                new LoadPicFromURL((ImageView) itemHolder.albumArt)
                        .execute(songOnline.imgUrl);
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onViewRecycled(ExploreAdapter.ItemHolder itemHolder) {

    }

    @Override
    public int getItemCount() {
        return relatedPlaylist.size();
    }



    @Override
    public int getItemViewType(int position) {
        if (relatedPlaylist.get(position) instanceof OnlineSong)
            return 11;
        if (relatedPlaylist.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void updatePlaylist(List searchResults) {
        this.relatedPlaylist = searchResults;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, songartist, albumtitle, artisttitle, albumartist, albumsongcount, sectionHeader;
        protected ImageView albumArt, artistImage, menu;

        public ItemHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.songartist = (TextView) view.findViewById(R.id.song_artist);
            this.albumsongcount = (TextView) view.findViewById(R.id.album_song_count);
            this.artisttitle = (TextView) view.findViewById(R.id.artist_name);
            this.albumtitle = (TextView) view.findViewById(R.id.album_title);
            this.albumartist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);

            this.sectionHeader = (TextView) view.findViewById(R.id.section_header);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()) {
                case 0:
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long[] ret = new long[1];
                            ret[0] = ((Song) relatedPlaylist.get(getAdapterPosition())).id;
                            playAll(mContext, ret, 0, -1, TimberUtils.IdType.NA,
                                    false, (Song) relatedPlaylist.get(getAdapterPosition()), false);
                        }
                    }, 100);

                    break;
                case 11:
                    //TODO set new song from playlist
                    OnlineSong clickedSong = ((OnlineSong) relatedPlaylist.get(getAdapterPosition()));

                    String[] currentSongMeta = new String[4];
                    currentSongMeta[0] = clickedSong.songUrl;
                    currentSongMeta[1] = clickedSong.title;
                    currentSongMeta[2] = clickedSong.artistName;
                    currentSongMeta[3] = clickedSong.ytMusicPlyListID;
                    downloadButtonAnimation.resetButton();
                    downloadButtonAnimation.getDownloadButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DownloadSong downloadSong = new DownloadSong(currentSongMeta , mContext.getApplication(), downloadButtonAnimation);
                            downloadSong.startDownload();
                        }
                    });
                    StartVideoStream startVideoStream = new StartVideoStream(clickedSong.songUrl, videoView, mContext.getApplicationContext());
                    break;
                case 10:
                    break;
            }
        }

    }

}





