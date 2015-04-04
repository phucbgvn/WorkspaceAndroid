package com.phudx1.data.dao;

public class AudioPlay {
    public long id;
    public String title;
    public String artist;
    public String imageAlbum;
    public String fullPath;
    public String mPath;
    public int duration;

    public AudioPlay() {
    }

    public AudioPlay(long ids, String titles, String artists,
            String imageAlbums, String mFullPaths, int durations, String mPaths) {
        id = ids;
        title = titles;
        artist = artists;
        imageAlbum = imageAlbums;
        fullPath = mFullPaths;
        duration = durations;
        mPath = mPaths;
    }
}
