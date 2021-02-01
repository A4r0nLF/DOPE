package com.naman14.timber.models;

public class OnlineSongSearchResult {
    public final long albumId;
    public final String albumName;
    public final long artistId;
    public final String artistName;
    public final int duration;
    public final long id;
    public final String title;
    public final int trackNumber;
    public final String imgUrl;
    public final String songUrl;

    public OnlineSongSearchResult() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.trackNumber = -1;
        this.imgUrl = "";
        this.songUrl = "";
    }

    public OnlineSongSearchResult(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber, String imgUrl, String songUrl) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.imgUrl = imgUrl;
        this.songUrl = songUrl;
    }
}