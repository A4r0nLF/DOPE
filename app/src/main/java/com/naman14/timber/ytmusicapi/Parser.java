package com.naman14.timber.ytmusicapi;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Parser {
    private String tempParam;
    private String tempQuery;

    public Parser() {
        this.tempQuery = "";
        this.tempParam = "";
    }

    public String getTempParam() {
        return tempParam;
    }

    public String getTempQuery() {
        return tempQuery;
    }


    public ArrayList<String> parseSearchSuggestions(String json) {
        ArrayList<String> searchSuggestions = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonElement jsonElement = jsonObject.get("contents")
                    .getAsJsonArray().get(0)
                    .getAsJsonObject().get("searchSuggestionsSectionRenderer")
                    .getAsJsonObject().get("contents");
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {

                searchSuggestions.add(jsonArray.get(i).getAsJsonObject().get("searchSuggestionRenderer")
                        .getAsJsonObject().get("navigationEndpoint")
                        .getAsJsonObject().get("searchEndpoint").getAsJsonObject().get("query").getAsString()
                );
            }
        } catch (Exception ignored){}
            return searchSuggestions;
    }

    public ArrayList<OnlineSong> parseSearchResults(String json) {
        ArrayList<OnlineSong> searchResults = new ArrayList<>();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonElement jsonElement = jsonObject.get("contents")
                .getAsJsonObject().get("tabbedSearchResultsRenderer")
                .getAsJsonObject().get("tabs").getAsJsonArray().get(0)
                .getAsJsonObject().get("tabRenderer").getAsJsonObject().get("content")
                .getAsJsonObject().get("sectionListRenderer").getAsJsonObject().get("contents");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {

            try {
                JsonElement jsnElemnt = jsonArray.get(i).getAsJsonObject()
                        .getAsJsonObject().get("musicShelfRenderer")
                        .getAsJsonObject().get("title").getAsJsonObject().get("runs")
                        .getAsJsonArray().get(0).getAsJsonObject().get("text");
                //Only songs not videos are wanted
                if (jsnElemnt.getAsString().equals("Top-Ergebnis")) {

                    String songResType = jsonArray.getAsJsonArray().get(i)
                            .getAsJsonObject().get("musicShelfRenderer").getAsJsonObject().get("contents")
                            .getAsJsonArray().get(0).getAsJsonObject().get("musicResponsiveListItemRenderer")
                            .getAsJsonObject().get("flexColumns").getAsJsonArray().get(1).getAsJsonObject()
                            .get("musicResponsiveListItemFlexColumnRenderer").getAsJsonObject().get("text")
                            .getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject().get("text").toString();

                    if (songResType.equals("\"Titel\"")) {
                        searchResults.addAll(creatSongList(parseSongMeta((JsonObject) jsonArray.getAsJsonArray().get(i))));
                    }
                } else if (jsnElemnt.getAsString().equals("Titel")) {

                    searchResults.addAll(creatSongList(parseSongMeta((JsonObject) jsonArray.getAsJsonArray().get(i))));
                    parseMoreResParams(jsonArray.getAsJsonArray().get(i));
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return searchResults;
    }

    public ArrayList<OnlineSong> parsePlaylist(String json) {
        ArrayList<OnlineSong> playlist = new ArrayList<>();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray jsonArray = jsonObject.get("contents").getAsJsonObject().get("singleColumnMusicWatchNextResultsRenderer")
                .getAsJsonObject().get("tabbedRenderer").getAsJsonObject().get("watchNextTabbedResultsRenderer")
                .getAsJsonObject().get("tabs").getAsJsonArray().get(0).getAsJsonObject().get("tabRenderer")
                .getAsJsonObject().get("content").getAsJsonObject().get("musicQueueRenderer").getAsJsonObject()
                .getAsJsonObject().get("content").getAsJsonObject().get("playlistPanelRenderer")
                .getAsJsonObject().get("contents").getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            playlist.addAll(creatSongList(parsePlaylistMeta(jsonArray.get(i).getAsJsonObject())));
        }

        return playlist;
    }

    //return String[6] = [0]: Img Url, [1]: title, [2] acts, [3]: album, [4]: videoID, [5]:playlistID
    private String[][] parsePlaylistMeta(JsonObject jsonObject) {
        String[][] songMeta = new String[1][6];
        JsonObject temJsnObj = jsonObject.getAsJsonObject().get("playlistPanelVideoRenderer").getAsJsonObject();
        songMeta[0][0] = getAlbumCover(temJsnObj.get("thumbnail").getAsJsonObject().get("thumbnails").getAsJsonArray());
        songMeta[0][1] = temJsnObj.get("title").getAsJsonObject().get("runs").getAsJsonArray().get(0)
                .getAsJsonObject().get("text").getAsString();

        JsonArray jsonArray1 = temJsnObj.get("longBylineText").getAsJsonObject().get("runs").getAsJsonArray();
        songMeta[0][2] = getActs(jsonArray1, 5, 0);
        songMeta[0][3] = getAlbum(jsonArray1);
        songMeta[0][4] = getID("videoId", temJsnObj);
        songMeta[0][5] = getID("playlistId", temJsnObj);

        return songMeta;
    }

    //return String[6] = [0]: Img Url, [1]: title, [2] acts, [3]: album, [4]: videoID, [5]:playlistID
    private String[][] parseSongMeta(JsonObject jsonObject) {
        String[][] songMeta = null;
        JsonArray tempJson0 = jsonObject.get("musicShelfRenderer")
                .getAsJsonObject().get("contents").getAsJsonArray();
        songMeta = new String[tempJson0.size()][6];



        for (int i = 0; i < tempJson0.size(); i++) {
            JsonArray tempJson1 = tempJson0.get(i)
                    .getAsJsonObject().get("musicResponsiveListItemRenderer").getAsJsonObject().get("thumbnail")
                    .getAsJsonObject().get("musicThumbnailRenderer").getAsJsonObject().get("thumbnail")
                    .getAsJsonObject().get("thumbnails").getAsJsonArray();
            songMeta[i][0] = getAlbumCover(tempJson1);

            JsonArray tempJson2 = jsonObject.get("musicShelfRenderer").getAsJsonObject().get("contents").getAsJsonArray().get(i)
                    .getAsJsonObject().get("musicResponsiveListItemRenderer").getAsJsonObject().get("flexColumns").getAsJsonArray();

            JsonObject tempJson3 = tempJson2.get(0).getAsJsonObject().get("musicResponsiveListItemFlexColumnRenderer")
                    .getAsJsonObject().get("text").getAsJsonObject().get("runs").getAsJsonArray().get(0).getAsJsonObject();
            songMeta[i][1] = tempJson3.get("text").getAsString();
            songMeta[i][4] = getID("videoId", tempJson3);
            songMeta[i][5] = getID("playlistId", tempJson3);

            JsonArray tempJson4 = tempJson2.get(1).getAsJsonObject().get("musicResponsiveListItemFlexColumnRenderer")
                    .getAsJsonObject().get("text").getAsJsonObject().get("runs").getAsJsonArray();

            songMeta[i][2] = getActs(tempJson4, 7, 2);
            songMeta[i][3] = getAlbum(tempJson4);
        }

        return songMeta;
    }

    private String getActs(JsonArray jsonArray, int minArraySize, int posfirstAct) {
        String acts = "";
        int numbActs = (jsonArray.size() - minArraySize) / 2 + 1;
        int i1 = 0;
        int i2 = posfirstAct;
        while (i1 < numbActs) {
            if (i1 > 0) acts = acts + " & ";
            acts = acts + jsonArray.get(i2).getAsJsonObject().get("text").getAsString();
            i1++;
            i2 += 2;
        }
        return acts;
    }

    private String getAlbum(JsonArray jsonArray) {
        return jsonArray.get(jsonArray.size() - 3).getAsJsonObject().get("text").getAsString();
    }

    private String getAlbumCover(JsonArray jsonArray) {
        return jsonArray.get(jsonArray.size() - 1).getAsJsonObject().get("url").getAsString();
    }

    private String getID(String idType, JsonObject jsonObject) {
        return jsonObject.get("navigationEndpoint").getAsJsonObject().get("watchEndpoint").getAsJsonObject().get(idType).getAsString();
    }

    private ArrayList<OnlineSong> creatSongList(String[][] multSongArray) {
        ArrayList<OnlineSong> onlineSongArrayList = new ArrayList<>();
        for (String[] songArray : multSongArray) {
            onlineSongArrayList.add(new OnlineSong(songArray[1], songArray[2], songArray[3], 0,
                    songArray[0], "https://music.youtube.com/watch?v=" + songArray[4],songArray[4], songArray[5]));
        }
        return onlineSongArrayList;
    }

    private void parseMoreResParams(JsonElement jsonElement) {
        JsonObject tempjsonObj = jsonElement.getAsJsonObject().get("musicShelfRenderer").getAsJsonObject()
                .get("bottomEndpoint").getAsJsonObject().get("searchEndpoint").getAsJsonObject();

        tempQuery = tempjsonObj.get("query").getAsString();
        tempParam = tempjsonObj.get("params").getAsString();
    }

}
