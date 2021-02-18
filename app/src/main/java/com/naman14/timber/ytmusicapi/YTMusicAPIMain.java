package com.naman14.timber.ytmusicapi;

import android.os.AsyncTask;
import android.util.Log;

import com.naman14.timber.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class YTMusicAPIMain extends AsyncTask<String, Void, ArrayList<Object>> {
    Parser parser;
    RequestJSON requestJSON;
    ArrayList<Object> objects;
    SearchAdapter adapter;


    public YTMusicAPIMain(ArrayList<Object> objects, SearchAdapter adapter, Parser parser, RequestJSON requestJSON) {
        this.parser = parser;
        this.requestJSON = requestJSON;
        this.objects = objects;
        this.adapter = adapter;

    }

    protected ArrayList<Object> doInBackground(String... searchQuery) {
        //Log.e("Res", "\n " + parser.parseSearchResults(requestJSON.getSearchResult("samra")));

        objects.add("Youtube Music Songs");
        objects.addAll(parser.parseSearchResults(requestJSON.getSearchResult(searchQuery[0])));

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
        adapter.updateSearchResults(objects);
        adapter.notifyDataSetChanged();
    }
}