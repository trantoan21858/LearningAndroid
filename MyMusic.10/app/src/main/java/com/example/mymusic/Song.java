package com.example.mymusic;

import android.graphics.Bitmap;

//Class Song
public class Song {
    public String title;
    public String artist;
    public long duration;
    public String data;

    public Song(String title, String artist, long duration, String data) {
        this.title=title;
        this.artist=artist;
        this.duration=duration;
        this.data=data;
    }
}
