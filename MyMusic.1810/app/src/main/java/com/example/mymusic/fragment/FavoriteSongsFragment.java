package com.example.mymusic.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.mymusic.ActivityMusic;
import com.example.mymusic.FavoriteDatabase.FavoriteSongsProvider;
import com.example.mymusic.Song;

import java.util.ArrayList;

public class FavoriteSongsFragment extends BaseSongListFragment {


    public static FavoriteSongsFragment newInstance(){
        return  new FavoriteSongsFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public ArrayList<Song> getList() {
        String stringQuery= FavoriteSongsProvider.URI_FAVORITE;
        ActivityMusic activity = (ActivityMusic) getActivity();
        ArrayList<Song> listAll = activity.getmList();
        ArrayList<Song> listFavorite = new ArrayList<>();
        Cursor cursor = activity.getContentResolver().query(Uri.parse(stringQuery),null,null,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            for (Song item : listAll){
                if (item.id == cursor.getInt(1) && cursor.getInt(2) ==2) {
                    listFavorite.add(item);
                }
            }
        }
        return listFavorite;
    }
}
