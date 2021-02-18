package com.naman14.timber.ytmusicapi;

public class OnlineSong {
    public final String albumName;
    public final String artistName;
    public final int duration;
    public final String title;
    public final String imgUrl;
    public final String songUrl;
    public final String ytMusicID;
    public final String ytMusicPlyListID;

    public OnlineSong() {
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.imgUrl = "";
        this.songUrl = "";
        this.ytMusicID="";
        this.ytMusicPlyListID="";
    }

    public OnlineSong( String title, String artistName, String albumName, int duration,
                       String imgUrl, String songUrl, String ytMusicID, String ytMusicPlyListID) {
        this.title = title;
        this.artistName = artistName;
        this.albumName = albumName;
        this.duration = duration;
        this.imgUrl = imgUrl;
        this.songUrl = songUrl;
        this.ytMusicID= ytMusicID;
        this.ytMusicPlyListID= ytMusicPlyListID;
    }


    @Override
    public String toString() {
        return "OnlineSong{" +
                "albumName='" + albumName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", duration=" + duration +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", ytMusicID='" + ytMusicID + '\'' +
                ", ytMusicPlyListID='" + ytMusicPlyListID + '\'' +
                '}';
    }
}
