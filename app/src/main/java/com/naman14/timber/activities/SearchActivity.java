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

package com.naman14.timber.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuItemCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.tabs.TabLayout;
import com.naman14.timber.R;
import com.naman14.timber.adapters.SearchAdapter;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.ytmusicapi.*;
import com.naman14.timber.models.Song;
import com.naman14.timber.provider.SearchHistory;
import com.naman14.timber.utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnTouchListener {

    private final Executor mSearchExecutor = Executors.newSingleThreadExecutor();
    @Nullable
    private AsyncTask mSearchTask = null;

    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;
    private SimpleCursorAdapter simpleCursorAdapter;


    private SearchAdapter adapter;

    private List<Object> searchResults = Collections.emptyList();
    private TabLayout tabLayout;
    private boolean localSearch;

    private Parser parser;
    private RequestJSON requestJSON;


    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        int layout = R.layout.suggestion_list_item;
        if (PreferencesUtility.getInstance(this).getTheme().equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            layout = R.layout.suggestion_list_item_dark;
        }

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        final String[] from = new String[]{"searchSuggestions"};
        final int[] to = new int[]{android.R.id.text1};
        simpleCursorAdapter = new SimpleCursorAdapter(this,
                layout,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);


        if (savedInstanceState != null && savedInstanceState.containsKey("QUERY_STRING")) {
            bundle = savedInstanceState;
        }


        localSearch = true;
        tabLayout = findViewById(R.id.search_location);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    localSearch = true;
                    onQueryTextChange(queryString);
                } else if (tab.getPosition() == 1) {
                    localSearch = false;
                    onQueryTextChange(queryString);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        parser = new Parser();
        requestJSON = new RequestJSON();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (queryString != null) {
            outState.putString("QUERY_STRING", queryString);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_library));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);
        if (PreferencesUtility.getInstance(this).getTheme().equals("dark"))
            // mSearchView.(getColor(R.color.black_800));


            MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    finish();
                    return false;
                }
            });

        menu.findItem(R.id.menu_search).expandActionView();

        if (bundle != null && bundle.containsKey("QUERY_STRING")) {
            mSearchView.setQuery(bundle.getString("QUERY_STRING"), true);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);

        mSearchView.setSuggestionsAdapter(simpleCursorAdapter);

        // Getting selected (clicked) item suggestion
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("searchSuggestions"));
                mSearchView.setQuery(txt, true);

                // search results from Youtube Music API
                ArrayList<Object> objects = new ArrayList<>();
                new YTMusicAPIMain(objects, adapter, parser, requestJSON, simpleCursorAdapter, 1)
                        .execute(mSearchView.getQuery().toString());

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {

        if (localSearch) {
            onQueryTextChange(query);
        } else {
            // search results from Youtube Music API
            ArrayList<Object> objects = new ArrayList<>();
            new YTMusicAPIMain(objects, adapter, parser, requestJSON, simpleCursorAdapter, 1)
                    .execute(queryString);
        }
        hideInputManager();

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        if (localSearch) {

            if (mSearchTask != null) {
                mSearchTask.cancel(false);
                mSearchTask = null;
            }
            queryString = newText;
            if (queryString.trim().equals("")) {
                searchResults.clear();
                adapter.updateSearchResults(searchResults);
                adapter.notifyDataSetChanged();
            } else {
                mSearchTask = new SearchTask().executeOnExecutor(mSearchExecutor, queryString);
            }
        } else { //Youtube Music search
            searchResults.clear();
            adapter.updateSearchResults(searchResults);
            adapter.notifyDataSetChanged();

            queryString = newText;
            if (queryString.trim().equals("")) {
                searchResults.clear();
                adapter.updateSearchResults(searchResults);
                adapter.notifyDataSetChanged();
            } else {
                // search suggestions from Youtube Music API
                ArrayList<Object> objects = new ArrayList<>();
                new YTMusicAPIMain(objects, adapter, parser, requestJSON, simpleCursorAdapter, 0)
                        .execute(newText);
            }
        }

        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideInputManager();
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mSearchTask != null && mSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            mSearchTask.cancel(false);
        }
        super.onDestroy();
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();

            SearchHistory.getInstance(this).addSearchString(queryString);
        }
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(String... params) {
            ArrayList<Object> results = new ArrayList<>(27);
            List<Song> songList = SongLoader.searchSongs(SearchActivity.this, params[0], 10);
            if (!songList.isEmpty()) {
                results.add(getString(R.string.songs));
                results.addAll(songList);
            }
            boolean canceled = isCancelled();
            if (canceled) {
                return null;
            }
            List<Album> albumList = AlbumLoader.getAlbums(SearchActivity.this, params[0], 7);
            if (!albumList.isEmpty()) {
                results.add(getString(R.string.albums));
                results.addAll(albumList);
            }

            canceled = isCancelled();
            if (canceled) {
                return null;
            }
            List<Artist> artistList = ArtistLoader.getArtists(SearchActivity.this, params[0], 7);
            if (!artistList.isEmpty()) {
                results.add(getString(R.string.artists));
                results.addAll(artistList);
            }
            if (results.size() == 0) {
                results.add(getString(R.string.nothing_found));
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> objects) {
            super.onPostExecute(objects);
            mSearchTask = null;
            if (objects != null) {
                adapter.updateSearchResults(objects);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
