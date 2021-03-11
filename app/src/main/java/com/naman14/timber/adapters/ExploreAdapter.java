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


import com.naman14.timber.R;
import com.naman14.timber.dataloaders.LoadPicFromURL;


import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.ytmusicapi.OnlineSong;


import java.util.Collections;
import java.util.List;

public class ExploreAdapter extends BaseSongAdapter<ExploreAdapter.ItemHolder> {

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public ExploreAdapter(Activity context) {
        this.mContext = context;

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
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
                ExploreAdapter.ItemHolder ml3 = new ExploreAdapter.ItemHolder(v3);
                return ml3;
        }
    }

    @Override
    public void onBindViewHolder(final ExploreAdapter.ItemHolder itemHolder, int i) {
        switch (getItemViewType(i)) {

            case 10:
                itemHolder.sectionHeader.setText((String) searchResults.get(i));
                break;
            case 11:
                OnlineSong songOnline = (OnlineSong) searchResults.get(i);
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
        return searchResults.size();
    }



    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof OnlineSong)
            return 11;
        if (searchResults.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void updatePlaylist(List searchResults) {
        this.searchResults = searchResults;
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
                            ret[0] = ((Song) searchResults.get(getAdapterPosition())).id;
                            playAll(mContext, ret, 0, -1, TimberUtils.IdType.NA,
                                    false, (Song) searchResults.get(getAdapterPosition()), false);
                        }
                    }, 100);

                    break;
                case 1:
                    NavigationUtils.goToAlbum(mContext, ((Album) searchResults.get(getAdapterPosition())).id);
                    break;
                case 2:
                    NavigationUtils.goToArtist(mContext, ((Artist) searchResults.get(getAdapterPosition())).id);
                    break;
                case 3:
                    break;
                case 11:
                    //TODO intent to open song online for testing...
                    //NavigationUtils.goToExplore(mContext, ((OnlineSong) searchResults.get(getAdapterPosition())));
                    Log.e("New", "Intent lol");
                    break;
                case 10:
                    break;
            }
        }

    }

}





