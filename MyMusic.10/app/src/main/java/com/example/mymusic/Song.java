package com.example.mymusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;


public class Song {

    public String title;

    public String artist;

    public long duration;

    public String data;

    public int id;

    public int count=0;
    public int isFavorite = 0;

    public Song(String title, String artist, long duration, String data,int id) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.data = data;
        this.id = id;
    }
    public static Bitmap getAlbumImage(String data)
    {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(data);
        Bitmap bitmap;
        byte [] dataImage = mmr.getEmbeddedPicture();

        // convert the byte array to a bitmap
        if(dataImage != null)
        {
            bitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
        }
        else
        {
            bitmap = null;
        }
         return bitmap;
    }


}
