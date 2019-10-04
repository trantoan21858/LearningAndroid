package com.example.mymusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.widget.LinearLayout;

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
