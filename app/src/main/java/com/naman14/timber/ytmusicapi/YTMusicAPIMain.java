package com.naman14.timber.ytmusicapi;

import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Adapter;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.naman14.timber.adapters.BaseSongAdapter;
import com.naman14.timber.adapters.ExploreAdapter;
import com.naman14.timber.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class YTMusicAPIMain extends AsyncTask<String, Void, ArrayList<Object>> {
    Parser parser;
    RequestJSON requestJSON;
    ArrayList<Object> objects;
    BaseSongAdapter adapter;
    SimpleCursorAdapter sCAdapter;
    private final int mode;


    //mode 0 = get search suggestions; mode 1 = get search Results
    //mode 2 = get more search Results; mode 3 = get Playlist
    public YTMusicAPIMain(ArrayList<Object> objects, SearchAdapter adapter, Parser parser, RequestJSON requestJSON, SimpleCursorAdapter sCAdapter, int mode) {
        this.parser = parser;
        this.requestJSON = requestJSON;
        this.objects = objects;
        this.adapter = adapter;
        this.mode = mode;
        this.sCAdapter = sCAdapter;
    }

    public YTMusicAPIMain(ArrayList<Object> objects, ExploreAdapter adapter, Parser parser, RequestJSON requestJSON, int mode) {
        this.parser = parser;
        this.requestJSON = requestJSON;
        this.objects = objects;
        this.adapter = adapter;
        this.mode = mode;
        this.sCAdapter = null;
    }

    protected ArrayList<Object> doInBackground(String... searchQuery) {
        //Log.e("Res", "\n " + parser.parseSearchResults(requestJSON.getSearchResult("samra")));

        objects.add("Youtube Music Songs");



        switch (mode){
            case 0:
                objects.clear();
                objects.addAll(parser.parseSearchSuggestions(requestJSON.getSearchSuggestions(searchQuery[0])));
                break;
            case 1:
                objects.addAll(parser.parseSearchResults(requestJSON.getSearchResult(searchQuery[0])));
                break;
            case 2:
                objects.addAll(parser.parseSearchResults(requestJSON.getMoreSearchResult(searchQuery[0],searchQuery[1])));
                break;
            case 3:
                objects.addAll(parser.parsePlaylist(requestJSON.getPlaylist(searchQuery[0], searchQuery[1])));
                break;
            default:
                Log.e("Error:"," unsupported mode number!");
        }

        //TestData
        //objects.add(new OnlineSong("KRIMINELL",
        //        "Kianush", "CROSSOVER", -1,
        //        "https://lh3.googleusercontent.com/Sw48WMfik1RtqP_XUvITUdm4F0Rwi-2IosgcNPkvq2xmh5Iq3NKlCo-mKGVM4Fge7P2rng7m33ebUA0_wA=w544-h544-l90-rj",
        //        "https://music.youtube.com/watch?v=oyF1GUjQZUU",
        //        "oyF1GUjQZUU",
        //        "RDAMVMfXreChsgHz0"
        //));
        //objects.add(new OnlineSong("Neptun",
        //        "KC REbell & Raf Camora", "Nepton", -1,
        //        "https://lh3.googleusercontent.com/yeJQtyrTQuHaYCu6uSEFUSXYwGdY7fa55CikKuL3KckVF1B9hhw43BTCmHL85jNpl7F1sSjxMTPxm8Ij=w544-h544-l90-rj",
        //        "https://music.youtube.com/watch?v=7f-y544iyQg",
        //        "7f-y544iyQg",
        //        "RDAMVM7f-y544iyQg"
        //));
        return objects;
    }

    @Override
    protected void onPostExecute(ArrayList<Object> objects) {
        switch (mode){
            case 0:
                if(!objects.isEmpty()) populateAdapter( objects);
                break;
            case 1:
                SearchAdapter searchAdapter = (SearchAdapter)adapter;
                searchAdapter.updateSearchResults(objects);
                searchAdapter.notifyDataSetChanged();
                break;
            case 2:

                break;
            case 3:
                ExploreAdapter exploreAdapter = (ExploreAdapter) adapter;
                exploreAdapter.updatePlaylist(objects);
                exploreAdapter.notifyDataSetChanged();
                break;
            default:
                Log.e("Error:"," unsupported mode number!");
        }


    }

    private void populateAdapter(ArrayList<Object> objects) {


        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "searchSuggestions"});
        for (int i = 0; i < objects.size(); i++) {
            c.addRow(new Object[]{i, objects.get(i)});
        }
        sCAdapter.changeCursor(c);
    }

}