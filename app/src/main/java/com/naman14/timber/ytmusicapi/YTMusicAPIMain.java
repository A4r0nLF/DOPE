package com.naman14.timber.ytmusicapi;

import android.os.AsyncTask;
import android.util.Log;

public class YTMusicAPIMain extends AsyncTask<String, Void, Void> {
    Parser parser;
    RequestJSON requestJSON;


    public YTMusicAPIMain() {
        parser = new Parser();
        requestJSON = new RequestJSON();

    }

    protected Void doInBackground(String... searchQuery) {
        Log.e("Res", "\n " + parser.parseSearchResults(requestJSON.getSearchResult("samra")));
        return null;
    }


}